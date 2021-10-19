package edu.baylor.ecs.cloudhubs.radsource.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import edu.baylor.ecs.cloudhubs.radsource.model.MessageQueue;
import edu.baylor.ecs.cloudhubs.radsource.model.RestEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * author: Abdullah Al Maruf
 * date: 10/15/21
 * time: 5:00 AM
 * website : https://maruftuhin.com
 */

@Service
@Slf4j
public class MQConsumerService {
    public List<MessageQueue> findConsumers(File sourceFile) throws IOException {
        List<MessageQueue> consumers = new ArrayList<>();

        CompilationUnit cu = StaticJavaParser.parse(sourceFile);

        String packageName = Helper.findPackage(cu);
        log.debug("package: " + packageName);

        // don't analyse further if no RestController import exists
        if (!hasEnableJMS(cu)) {
            log.debug("no JMSListener found");
            return consumers;
        }

//        String packageName = Helper.findPackage(cu);
//        log.debug("package: " + packageName);

        // loop through class declarations
        for (ClassOrInterfaceDeclaration cid : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String className = cid.getNameAsString();
            log.debug("class: " + className);

            AnnotationExpr cae = cid.getAnnotationByName("EnableJms").orElse(null);
            String classLevelPath = queueNameFromAnnotation(cae);
            log.debug("class-level-path: " + classLevelPath);

            // loop through methods
            for (MethodDeclaration md : cid.findAll(MethodDeclaration.class)) {
                String methodName = md.getNameAsString();
                log.debug("method: " + methodName);

                // loop through annotations
                for (AnnotationExpr ae : md.getAnnotations()) {
                    MessageQueue consumer = new MessageQueue();

                    if (!ae.getNameAsString().equals("JmsListener")) {
                        continue;
                    }

                    String queue = queueNameFromAnnotation(ae);
                    log.debug("method-level-queue: " + queue);

                    consumer.setSource(sourceFile.getCanonicalPath());
                    consumer.setParentMethod(packageName + "." + className + "." + methodName);
                    consumer.setQueueName(queue);

                    log.debug("consumer: " + consumer);

                    consumers.add(consumer);
                }
            }
        }


        return consumers;
    }

    // populate return type in consumer
//    private void resolveReturnTypeForMethodDeclaration(CompilationUnit cu, MethodDeclaration md, RestEndpoint restEndpoint) {
//        String returnType = md.getTypeAsString();
//
//        log.debug("return-type: " + md.getTypeAsString());
//
//        if (returnType.startsWith("List<") && returnType.endsWith(">")) {
//            returnType = returnType.replace("List<", "").replace(">", "");
//            restEndpoint.setCollection(true);
//        } else if (returnType.endsWith("[]")) {
//            returnType = returnType.replace("[]", "");
//            restEndpoint.setCollection(true);
//        }
//
//        restEndpoint.setReturnType(Helper.findFQClassName(cu, returnType));
//    }

    // populate arguments in restEndpoint
    // TODO: find FQ name?
    private void resolveArgumentsForMethodDeclaration(CompilationUnit cu, MethodDeclaration md, RestEndpoint restEndpoint) {
        String arguments = md.getParameters().toString();
        log.debug("arguments: " + arguments);
        restEndpoint.setArguments(arguments);
    }

    private boolean hasEnableJMS(CompilationUnit cu) {
        Boolean enableJMS = false;
        Boolean jmsListener = false;

        for (ImportDeclaration id : cu.findAll(ImportDeclaration.class)) {
            if (id.getNameAsString().equals("org.springframework.jms.annotation.EnableJms"))
                enableJMS = true;
            if (id.getNameAsString().equals("org.springframework.jms.annotation.JmsListener"))
                jmsListener = true;
            if (enableJMS && jmsListener) break;
        }
        return enableJMS && jmsListener;
    }

    private String queueNameFromAnnotation(AnnotationExpr ae) {
        if (ae == null) {
            return "";
        }

        log.debug("annotation-model: " + ae.getMetaModel());

        if (ae.isSingleMemberAnnotationExpr()) {
            return Helper.removeEnclosedQuotations(ae.asSingleMemberAnnotationExpr().getMemberValue().toString());
        }

        if (ae.isNormalAnnotationExpr() && ae.asNormalAnnotationExpr().getPairs().size() > 0) {
            for (MemberValuePair mvp : ae.asNormalAnnotationExpr().getPairs()) {
                if (mvp.getName().toString().equals("destination")) {
                    return Helper.removeEnclosedQuotations(mvp.getValue().toString());
                }
            }
        }
        return "";
    }
}
