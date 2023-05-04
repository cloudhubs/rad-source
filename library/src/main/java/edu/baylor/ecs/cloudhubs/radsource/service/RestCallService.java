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
import edu.baylor.ecs.cloudhubs.radsource.model.RestCall;
import edu.baylor.ecs.cloudhubs.radsource.model.RestTemplateMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import java.nio.charset.StandardCharsets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class RestCallService {
	
	public List<RestCall> extractRestCalls(String jsonFilePath) throws IOException {
		List<RestCall> restCalls = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		String jsonData = readFile(jsonFilePath);
		List<ExtractedServiceData> extractedServiceData = Arrays.asList(mapper.readValue(jsonData, ExtractedServiceData[].class));
		
		for (ExtractedServiceData serviceData : extractedServiceData) {
			for (ExtractedServiceData.EndpointData endpointCallData : serviceData.getCallsData().getEndpoints()) {
				RestCall restCall = new RestCall();
//				restCall.setSource(sourceFile.getCanonicalPath());
//                restCall.setParentMethod(endpointCallData.getPath());
                restCall.setHttpMethod(endpointCallData.getMethodType());
                restCall.setReturnType(endpointCallData.getReturnType());
                restCall.setUrl(endpointCallData.getPath());
                restCall.setMsRoot(serviceData.getName());
                // Arguments param is not counted
				restCalls.add(restCall);
			}
			
			for (ExtractedServiceData.EventData eventData : serviceData.getCallsData().getEvents()) {
				RestCall restCall = new RestCall();
//				restCall.setSource(sourceFile.getCanonicalPath());
//                restCall.setParentMethod(packageName + "." + className + "." + methodName);
//                restCall.setHttpMethod(endpointCallData.getMethodType());
//                restCall.setReturnType(endpointCallData.getReturnType());
				restCall.setHttpMethod("EVENT");
                restCall.setUrl(eventData.getName());
                restCall.setMsRoot(serviceData.getName());
                // Arguments param is not counted
				restCalls.add(restCall);
			}
			
			for (ExtractedServiceData.GRPCData grpcCallData : serviceData.getCallsData().getGrpcList()) {
				RestCall restCall = new RestCall();
//				restCall.setSource(sourceFile.getCanonicalPath());
//                restCall.setParentMethod(packageName + "." + className + "." + methodName);
//                restCall.setHttpMethod(grpcCallData.getMethodType());
				restCall.setHttpMethod("RPC");
                restCall.setReturnType(grpcCallData.getReturnType());
                restCall.setUrl(grpcCallData.getName());
                restCall.setMsRoot(serviceData.getName());
                // Arguments param is not counted
				restCalls.add(restCall);
			}
		}
		return restCalls;
		
	}
	
	
	private String readFile(String fileName) {
		try {
			return FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
    public List<RestCall> findRestCalls(File sourceFile) throws IOException {
        List<RestCall> restCalls = new ArrayList<>();

        CompilationUnit cu = StaticJavaParser.parse(sourceFile);

        // don't analyse further if no RestTemplate import exists
        if (!hasRestTemplateImport(cu)) {
            log.debug("no RestTemplate found");
            return restCalls;
        }

        String packageName = Helper.findPackage(cu);
        log.debug("package: " + packageName);

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

                    RestTemplateMethod restTemplateMethod = RestTemplateMethod.findByName(methodCall);

                    if (restTemplateMethod != null) {
                        log.debug("method-call: " + methodCall);

                        Expression scope = mce.getScope().orElse(null);

                        log.debug("scope: " + scope);

                        // match field access
                        if (isRestTemplateScope(scope, cid)) {
                            // construct rest call
                            RestCall restCall = new RestCall();
                            restCall.setSource(sourceFile.getCanonicalPath());
                            restCall.setParentMethod(packageName + "." + className + "." + methodName);
                            restCall.setHttpMethod(restTemplateMethod.getHttpMethod().toString());

                            // get http methods for exchange method
                            if (restTemplateMethod.getMethodName().equals("exchange")) {
                                restCall.setHttpMethod(getHttpMethodForExchange(mce.getArguments().toString()));
                            }

                            // find return type
                            resolveReturnType(restCall, cu, mce, restTemplateMethod);

                            System.out.println("rest-call: --- " + restCall);
                            
                            // find url
                            restCall.setUrl(findUrl(mce, cid));

                            log.debug("rest-call: " + restCall);

                            // add to list of restCall
                            restCalls.add(restCall);
                        }
                    }
                }
            }
        }

        return restCalls;
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

    // populate return type in restCall
    private void resolveReturnType(RestCall restCall, CompilationUnit cu, MethodCallExpr mce, RestTemplateMethod restTemplateMethod) {
        log.debug("arguments: " + mce.getArguments());

        String returnType = null;
        boolean isCollection = false;

        if (mce.getArguments().size() > restTemplateMethod.getResponseTypeIndex()) {
            String param = mce.getArguments().get(restTemplateMethod.getResponseTypeIndex()).toString();

            if (param.endsWith(".class")) {
                param = param.replace(".class", "");
            }

            if (param.endsWith("[]")) {
                param = param.replace("[]", "");
                isCollection = true;
            }

            log.debug("param: " + param);
            returnType = Helper.findFQClassName(cu, param);
        }

        restCall.setReturnType(returnType);
        restCall.setCollection(isCollection);
    }
    
    
    /// TODO: If needed later for better approach - Not tested
    
//    Expression result = traverseExpression(exp);
//
//    if (result instanceof StringLiteralExpr) {
//      String finalResult = ((StringLiteralExpr) result).getValue();
//      System.out.println("RESULTS OF TRAVERSE ==== ");
//      System.out.println(finalResult);
//    }
//    
//    
//    private static Expression traverseExpression(Expression expr) {
//        if (expr instanceof StringLiteralExpr) {
//          return expr;
//        } else if (expr instanceof BinaryExpr) {
//          BinaryExpr binaryExpr = (BinaryExpr) expr;
//          Expression left = traverseExpression(binaryExpr.getLeft());
//          Expression right = traverseExpression(binaryExpr.getRight());
//          String operator = binaryExpr.getOperator().asString();
//
//          if (left instanceof StringLiteralExpr && right instanceof StringLiteralExpr) {
//            String leftString = ((StringLiteralExpr) left).getValue();
//            String rightString = ((StringLiteralExpr) right).getValue();
//            return new StringLiteralExpr(leftString + "/" + rightString);
//          } else if (left instanceof StringLiteralExpr && operator.equals("+")) {
//            String leftString = ((StringLiteralExpr) left).getValue();
//            return new BinaryExpr(leftString, right, BinaryExpr.Operator.PLUS);
//          } else if (right instanceof StringLiteralExpr && operator.equals("+")) {
//            String rightString = ((StringLiteralExpr) right).getValue();
//            return new BinaryExpr(left, rightString, BinaryExpr.Operator.PLUS);
//          }
//        }
//
//        return null;
//      } 

    private String findUrl(MethodCallExpr mce, ClassOrInterfaceDeclaration cid) {
    	System.out.println("Start to : FindURL" + mce.toString());
    	
        if (mce.getArguments().size() == 0) {
            return "";
        }

        Expression exp = mce.getArguments().get(0);
        log.debug("url-meta: " + exp.getMetaModel());
        log.debug("url-exp: " + exp.toString());
        
        
       
        System.out.println("url-meta: " + exp.getMetaModel());
        System.out.println("url-exp: " + exp.toString());
        
        

        if (exp.isStringLiteralExpr()) {
        	System.out.println("type-exp: isStringLiteralExpr");
            return Helper.removeEnclosedQuotations(exp.toString());
        } else if (exp.isFieldAccessExpr()) {
        	System.out.println("type-exp: isFieldAccessExpr");
            return fieldValue(cid, exp.asFieldAccessExpr().getNameAsString());
        } else if (exp.isNameExpr()) {
        	System.out.println("type-exp: isNameExpr");
            return fieldValue(cid, exp.asNameExpr().getNameAsString());
        } else if (exp.isBinaryExpr()) {
        	System.out.println("type-exp: isBinaryExpr");
//        	String[] stringParts = exp.toString().split("+");
//        	
//        	if (stringParts.length > 0 && (!stringParts[0].contains("/")) && !stringParts[0].contains("\"")) {
//        		
//        	}
        	
        	return resolveUrlFromBinaryExpModified(exp.asBinaryExpr());
//            return resolveUrlFromBinaryExp(exp.asBinaryExpr());
        }
        System.out.println("NO type-exp");
        return "";
    }

    private String resolveUrlFromBinaryExpModified(BinaryExpr exp) {
    	
    	String[] stringParts = exp.toString().split("+");
    	
    	System.out.println("URL PARTS BEFORE: " + stringParts.length);
    	
    	StringBuilder newURL = new StringBuilder();
    	
    	if (stringParts.length <= 0) {
    		return "";
    	} else {
    		// The first part is variable, assume it's inside_payment_service_url + "/api/v1/inside_pay_service/inside_payment/drawback/" + userId + "/" + money
        	if ((stringParts[0].contains("/"))) {//|| stringParts[0].contains("\"")) {
        		newURL.append(stringParts[0]);
        	}
        	for (int i = 1; i < stringParts.length; i++) {
        		if ((stringParts[i].contains("/"))) { //|| stringParts[i].contains("\"")) {
        			newURL.append(stringParts[i]);
        		} else {
        			newURL.append("/");
        			newURL.append("{var}");
        		}
        	}
    	}
    	
    	System.out.println("URL AFTER: " + newURL.toString());
    	
    	return newURL.toString();
    	
    }
    
    private boolean hasRestTemplateImport(CompilationUnit cu) {
        for (ImportDeclaration id : cu.findAll(ImportDeclaration.class)) {
            if (id.getNameAsString().equals("org.springframework.web.client.RestTemplate")) {
                return true;
            }
        }
        return false;
    }

    private boolean isRestTemplateScope(Expression scope, ClassOrInterfaceDeclaration cid) {
        if (scope == null) {
            return false;
        }

        // field access: this.restTemplate
        if (scope.isFieldAccessExpr() && isRestTemplateField(cid, scope.asFieldAccessExpr().getNameAsString())) {
            log.debug("field-access: " + scope.asFieldAccessExpr().getNameAsString());
            return true;
        }

        // filed access without this
        if (scope.isNameExpr() && isRestTemplateField(cid, scope.asNameExpr().getNameAsString())) {
            log.debug("name-expr: " + scope.asNameExpr().getNameAsString());
            return true;
        }

        return false;
    }

    private boolean isRestTemplateField(ClassOrInterfaceDeclaration cid, String fieldName) {
        for (FieldDeclaration fd : cid.findAll(FieldDeclaration.class)) {
            if (fd.getElementType().toString().equals("RestTemplate") &&
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

    private List<String> findAllRestTemplateFields(ClassOrInterfaceDeclaration cid) {
        List<String> fields = new ArrayList<>();

        for (FieldDeclaration fd : cid.findAll(FieldDeclaration.class)) {
            if (fd.getElementType().toString().equals("RestTemplate")) {
                for (VariableDeclarator variable : fd.getVariables()) {
                    fields.add(variable.getNameAsString());
                }
            }
        }

        return fields;
    }
}
