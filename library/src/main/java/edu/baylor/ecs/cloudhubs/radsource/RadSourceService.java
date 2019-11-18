package edu.baylor.ecs.cloudhubs.radsource;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RestCall;
import edu.baylor.ecs.cloudhubs.radsource.model.RestTemplateMethod;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class RadSourceService {

    public RadSourceResponseContext generateRadSourceResponseContext(RadSourceRequestContext request) throws IOException {
        RadSourceResponseContext responseContext = new RadSourceResponseContext();
        responseContext.setRequest(request);

        String filePath = request.getPathToSource();
        testTypeSolver(filePath);

        return responseContext;
    }

    private List<RestCall> testTypeSolver(String filePath) throws FileNotFoundException {
        List<RestCall> restCalls = new ArrayList<>();

        TypeSolver typeSolver = new CombinedTypeSolver();

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(filePath));

        String packageName = findPackage(cu);
        log.info("package: " + packageName);

        // loop through class declarations
        for (ClassOrInterfaceDeclaration cid : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String className = cid.getNameAsString();
            log.info("class: " + className);

            // loop through methods
            for (MethodDeclaration md : cid.findAll(MethodDeclaration.class)) {
                String methodName = md.getNameAsString();
                log.info("method: " + methodName);

                // loop through method calls
                for (MethodCallExpr mce : md.findAll(MethodCallExpr.class)) {
                    String methodCall = mce.getNameAsString();

                    RestTemplateMethod restTemplateMethod = RestTemplateMethod.findByName(methodCall);

                    if (restTemplateMethod != null) {
                        log.info("method-call: " + methodCall);

                        Expression scope = mce.getScope().orElse(null);

                        // match field access
                        if (scope != null && scope.isFieldAccessExpr() &&
                                matchFieldType(cid, scope.asFieldAccessExpr().getNameAsString(), "RestTemplate")) {

                            log.info("field-access: " + scope.asFieldAccessExpr().getNameAsString());

                            // everything matched here

                            // find return type

                            log.info("arguments: " + mce.getArguments());

                            String returnType = null;
                            boolean isCollection = false;

                            for (Expression ex : mce.getArguments()) {
                                String param = ex.toString();

                                if (param.endsWith(".class")) {
                                    param = param.replace(".class", "");
                                } else {
                                    continue;
                                }

                                if (param.endsWith("[]")) {
                                    param = param.replace("[]", "");
                                    isCollection = true;
                                }

                                log.info("param: " + param);
                                returnType = findFQClassName(cu, param);
                            }

                            // construct rest call

                            RestCall restCall = new RestCall();
                            restCall.setParentMethod(packageName + "." + className + "." + methodName);
                            restCall.setHttpMethod(restTemplateMethod.getHttpMethod().toString());
                            restCall.setReturnType(returnType);
                            restCall.setCollection(isCollection);

                            log.info("rest-call: " + restCall);
                            restCalls.add(restCall);
                        }

                    }
                }
            }
        }

        return restCalls;
    }

    private String findFQClassName(CompilationUnit cu, String param) {
        for (ImportDeclaration id : cu.findAll(ImportDeclaration.class)) {
            if (id.getNameAsString().endsWith(param)) {
                log.info("import: " + id.getNameAsString());
                return id.getNameAsString();
            }
        }
        return null;
    }

    private boolean matchFieldType(ClassOrInterfaceDeclaration cid, String fieldName, String type) {
        for (FieldDeclaration fd : cid.findAll(FieldDeclaration.class)) {
            if (fd.getElementType().toString().equals(type) &&
                    fd.getVariables().toString().contains(fieldName)) {

                return true;
            }
        }
        return false;
    }

    private List<String> findAllFieldsOfType(ClassOrInterfaceDeclaration cid, String type) {
        List<String> fields = new ArrayList<>();

        for (FieldDeclaration fd : cid.findAll(FieldDeclaration.class)) {
            if (fd.getElementType().toString().equals(type)) {
                for (VariableDeclarator variable : fd.getVariables()) {
                    fields.add(variable.getNameAsString());
                }
            }
        }

        return fields;
    }

    private String findPackage(CompilationUnit cu) {
        for (PackageDeclaration pd : cu.findAll(PackageDeclaration.class)) {
            return pd.getNameAsString();
        }
        return null;
    }

    private void symbolSolver(String filePath) throws IOException {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JarTypeSolver(filePath));
        combinedTypeSolver.add(new JavaParserTypeSolver(filePath));

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        // Parse some code
        CompilationUnit cu = StaticJavaParser.parse(readFileLines(filePath));

        JavaParserFacade javaParserFacade = JavaParserFacade.get(combinedTypeSolver);

        // Find all the calculations with two sides:
        cu.findAll(MethodCallExpr.class).forEach(be -> {
            SymbolReference<ResolvedMethodDeclaration> ref = javaParserFacade.solve(be);

            if (ref.isSolved()) {
                log.info(ref.getCorrespondingDeclaration().getQualifiedSignature());
            }
        });
    }

    private String readFileLines(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
