package edu.baylor.ecs.cloudhubs.radsource.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import edu.baylor.ecs.cloudhubs.radsource.model.JMSTemplateMethod;
import edu.baylor.ecs.cloudhubs.radsource.model.MessageQueue;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
@Slf4j
public class MQProducerService {
    public List<MessageQueue> findProducers(File sourceFile) throws IOException {
        List<MessageQueue> producers = new ArrayList<>();

        CompilationUnit cu = StaticJavaParser.parse(sourceFile);

        String packageName = Helper.findPackage(cu);
        log.debug("package: " + packageName);

//         don't analyse further if no jmsTemplate import exists
        if (!hasJMSTemplateImport(cu)) {
            log.debug("no JMSTemplate found");
            return producers;
        }

//        String packageName = Helper.findPackage(cu);
//        log.debug("package: " + packageName);

        // loop through class declarations
        for (ClassOrInterfaceDeclaration cid : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String className = cid.getNameAsString();
            log.debug("class: " + className);

            // loop through methods
            for (MethodDeclaration md : cid.findAll(MethodDeclaration.class)) {
                String methodName = md.getNameAsString();
                log.debug("method: " + methodName);

                // loop through method calls
                for (MethodCallExpr mce : md.findAll(MethodCallExpr.class)) {
                    String methodCall = mce.getNameAsString();

                    JMSTemplateMethod jmsTemplateMethod = JMSTemplateMethod.findByName(methodCall);

                    if (jmsTemplateMethod != null) {
                        log.debug("method-call: " + methodCall);

                        Expression scope = mce.getScope().orElse(null);

                        log.debug("scope: " + scope);

                        // match field access
                        if (isJmsTemplateScope(scope, cid)) {
                            // construct rest call
                            MessageQueue producer = new MessageQueue();
                            producer.setSource(sourceFile.getCanonicalPath());
                            producer.setParentMethod(packageName + "." + className + "." + methodName);

                            // find url
                            producer.setQueueName(findUrl(mce, cid));

                            log.debug("MQ-Producer: " + producer);

                            // add to list of producer
                            producers.add(producer);
                        }
                    }
                }
            }
        }

        return producers;
    }

    private String getHttpMethodForExchange(String arguments) {
        if (arguments.contains("HttpMethod.POST")) {
            return "POST";
        } else if (arguments.contains("HttpMethod.PUT")) {
            return "PUT";
        } else if (arguments.contains("HttpMethod.DELETE")) {
            return "DELETE";
        } else {
            return "GET"; // default
        }
    }

    private String findUrl(MethodCallExpr mce, ClassOrInterfaceDeclaration cid) {
        if (mce.getArguments().size() == 0) {
            return "";
        }

        Expression exp = mce.getArguments().get(0);
        log.debug("url-meta: " + exp.getMetaModel());
        log.debug("url-exp: " + exp.toString());

        if (exp.isStringLiteralExpr()) {
            return Helper.removeEnclosedQuotations(exp.toString());
        } else if (exp.isFieldAccessExpr()) {
            return fieldValue(cid, exp.asFieldAccessExpr().getNameAsString());
        } else if (exp.isNameExpr()) {
            return fieldValue(cid, exp.asNameExpr().getNameAsString());
        } else if (exp.isBinaryExpr()) {
            return resolveUrlFromBinaryExp(exp.asBinaryExpr());
        }

        return "";
    }

    private boolean hasJMSTemplateImport(CompilationUnit cu) {
        for (ImportDeclaration id : cu.findAll(ImportDeclaration.class)) {
            if (id.getNameAsString().equals("org.springframework.jms.core.JmsTemplate")) {
                return true;
            }
        }
        return false;
    }

    private boolean isJmsTemplateScope(Expression scope, ClassOrInterfaceDeclaration cid) {
        if (scope == null) {
            return false;
        }

        // field access: this.restTemplate
        if (scope.isFieldAccessExpr() && isJmsTemplateField(cid, scope.asFieldAccessExpr().getNameAsString())) {
            log.debug("field-access: " + scope.asFieldAccessExpr().getNameAsString());
            return true;
        }

        // filed access without this
        if (scope.isNameExpr() && isJmsTemplateField(cid, scope.asNameExpr().getNameAsString())) {
            log.debug("name-expr: " + scope.asNameExpr().getNameAsString());
            return true;
        }

        return false;
    }

    private boolean isJmsTemplateField(ClassOrInterfaceDeclaration cid, String fieldName) {
        for (FieldDeclaration fd : cid.findAll(FieldDeclaration.class)) {
            if (fd.getElementType().toString().equals("JmsTemplate") &&
                    fd.getVariables().toString().contains(fieldName)) {

                return true;
            }
        }
        return false;
    }

    private String fieldValue(ClassOrInterfaceDeclaration cid, String fieldName) {
        for (FieldDeclaration fd : cid.findAll(FieldDeclaration.class)) {
            if (fd.getVariables().toString().contains(fieldName)) {
                Expression init = fd.getVariable(0).getInitializer().orElse(null);
                if (init != null) {
                    return Helper.removeEnclosedQuotations(init.toString());
                }
            }
        }
        return "";
    }

    // TODO: resolve recursively; kind of resolved, probably not every case considered
    private String resolveUrlFromBinaryExp(BinaryExpr exp) {
        // this handles cases in the form of "some/path/here/" + someExpression + "/" someOtherExpression, or just
        // "some/path/here/" + someExpression
        String trailer = exp.getRight().toString().equals("/") || exp.getRight().toString().equals("\"/\"") ? "" : "{var}";
        if (exp.getLeft().isBinaryExpr()) {
            return resolveUrlFromBinaryExp(exp.getLeft().asBinaryExpr()) + trailer;
        }
        return Helper.removeEnclosedQuotations(exp.getLeft().toString()) + trailer;
    }

    private List<String> findAllJmsTemplateFields(ClassOrInterfaceDeclaration cid) {
        List<String> fields = new ArrayList<>();

        for (FieldDeclaration fd : cid.findAll(FieldDeclaration.class)) {
            if (fd.getElementType().toString().equals("JmsTemplate")) {
                for (VariableDeclarator variable : fd.getVariables()) {
                    fields.add(variable.getNameAsString());
                }
            }
        }

        return fields;
    }
}
