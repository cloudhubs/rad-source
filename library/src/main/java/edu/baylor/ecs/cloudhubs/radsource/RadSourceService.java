package edu.baylor.ecs.cloudhubs.radsource;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class RadSourceService {
    private final List<String> restTemplateMethods = Arrays.asList(
            "getForObject", "getForEntity",
            "postForObject", "postForLocation",
            "delete", "put", "exchange"
    );

    public RadSourceResponseContext generateRadSourceResponseContext(RadSourceRequestContext request) throws IOException {
        RadSourceResponseContext responseContext = new RadSourceResponseContext();
        responseContext.setRequest(request);

        String filePath = request.getPathToSource();
        testTypeSolver(filePath);

        return responseContext;
    }

    private void testTypeSolver(String filePath) throws FileNotFoundException {
        TypeSolver typeSolver = new CombinedTypeSolver();

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(filePath));

        String packageName = findPackage(cu);
        log.info("package: " + packageName);

        // loop through class declarations
        for (ClassOrInterfaceDeclaration cid : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            log.info("class: " + cid.getNameAsString());

            // loop through methods
            for (MethodDeclaration md : cid.findAll(MethodDeclaration.class)) {
                log.info("method: " + md.getNameAsString());

                // loop through method calls
                for (MethodCallExpr mce : md.findAll(MethodCallExpr.class)) {
                    if (restTemplateMethods.contains(mce.getNameAsString())) {

                        log.info("method-call: " + mce.getNameAsString());

                        Expression scope = mce.getScope().orElse(null);

                        // match field access
                        if (scope != null && scope.isFieldAccessExpr() &&
                                matchFieldType(cid, scope.asFieldAccessExpr().getNameAsString(), "RestTemplate")) {

                            log.info("field-access: " + scope.asFieldAccessExpr().getNameAsString());

                            // everything matched here
                        }

                    }
                }
            }
        }
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
