package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.RestEntityContext;
import edu.baylor.ecs.cloudhubs.radsource.model.RestCall;
import edu.baylor.ecs.cloudhubs.radsource.model.RestEndpoint;
import edu.baylor.ecs.cloudhubs.radsource.model.RestFlow;

import java.util.ArrayList;
import java.util.List;

public class RestFlowService {

    public List<RestFlow> findRestFlows(List<RestEntityContext> restEntityContexts) {
        List<RestFlow> restFlows = new ArrayList<>();

        for (RestEntityContext contextA : restEntityContexts) {
            for (RestEntityContext contextB : restEntityContexts) {
                // don't match same MS
                if (contextA.getPathToMsRoot().equals(contextB.getPathToMsRoot())) continue;

                // consider contextA as clients and contextB as endpoints
                restFlows.addAll(restFlowsForContexts(contextA.getRestCalls(), contextB.getRestEndpoints()));
            }
        }

       /* // combine all clients into one list
        List<RestCall> restCalls = new ArrayList<>();
        restEntityContexts.forEach(e -> restCalls.addAll(e.getRestCalls()));

        // combine all endpoints into one list
        List<RestEndpoint> restEndpoints = new ArrayList<>();
        restEntityContexts.forEach(e -> restEndpoints.addAll(e.getRestEndpoints()));

        // match each pair
        for (RestCall restCall : restCalls) {
            for (RestEndpoint restEndpoint : restEndpoints) {
                // TODO
            }
        }*/

        return restFlows;
    }

    private List<RestFlow> restFlowsForContexts(List<RestCall> restCalls, List<RestEndpoint> restEndpoints) {
        List<RestFlow> restFlows = new ArrayList<>();

        for (RestCall restCall : restCalls) {
            for (RestEndpoint restEndpoint : restEndpoints) {
                if (restCall.getHttpMethod().equals(restEndpoint.getHttpMethod()) &&
                        isReturnTypeMatched(restCall.getReturnType(), restEndpoint.getReturnType()) &&
                        restCall.isCollection() == restEndpoint.isCollection()) {

                    restFlows.add(new RestFlow(restCall, restEndpoint));
                }
            }
        }

        return restFlows;
    }

    // match class name instead of FQ name
    private boolean isReturnTypeMatched(String returnTypeA, String returnTypeB) {
        returnTypeA = trimFQName(returnTypeA);
        returnTypeB = trimFQName(returnTypeB);

        if (returnTypeA.equals(returnTypeB)) {
            return true;
        } else {
            return matchIgnoringResponseEntity(returnTypeA, returnTypeB);
        }
    }

    // ignore ResponseEntity wrapper
    // ResponseEntity<Exam> should match with Exam
    private boolean matchIgnoringResponseEntity(String returnTypeA, String returnTypeB) {
        returnTypeA = trimResponseEntity(returnTypeA);
        returnTypeB = trimResponseEntity(returnTypeB);

        return returnTypeA.equals(returnTypeB);
    }

    private String trimFQName(String returnType) {
        if (returnType.contains(".")) {
            return returnType.substring(returnType.lastIndexOf('.') + 1);
        }
        return returnType;
    }

    private String trimResponseEntity(String returnType) {
        if (returnType.startsWith("ResponseEntity<") && returnType.endsWith(">")) {
            return returnType
                    .replaceAll("ResponseEntity<", "")
                    .replaceAll(">", "");
        }

        return returnType;
    }
}
