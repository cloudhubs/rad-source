package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.RestEntityContext;
import edu.baylor.ecs.cloudhubs.radsource.model.RestCall;
import edu.baylor.ecs.cloudhubs.radsource.model.RestEndpoint;
import edu.baylor.ecs.cloudhubs.radsource.model.RestFlow;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * author: Abdullah Al Maruf
 * date: 10/15/21
 * time: 5:01 AM
 * website : https://maruftuhin.com
 */

@Slf4j
public class MQFlowService {

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

        return restFlows;
    }

    private List<RestFlow> restFlowsForContexts(List<RestCall> restCalls, List<RestEndpoint> restEndpoints) {
        List<RestFlow> restFlows = new ArrayList<>();

        for (RestCall restCall : restCalls) {
            for (RestEndpoint restEndpoint : restEndpoints) {
                if (restCall.getHttpMethod().equals(restEndpoint.getHttpMethod()) &&
                        (isReturnTypeMatched(restCall, restEndpoint) || isPathMatched(restCall, restEndpoint))) {
                    restFlows.add(new RestFlow(restCall, restEndpoint));
                }
            }
        }

        return restFlows;
    }

    private boolean isPathMatched(RestCall restCall, RestEndpoint restEndpoint) {
        // use unified path variable {var}
        String serverPath = Helper.unifyPathVariable(restEndpoint.getPath());

        // get path from restCall url
        String clientPath = "/";
        try {
            clientPath = new URL(restCall.getUrl()).getPath();
        } catch (MalformedURLException e) {
            log.error(e.toString());
        }

        log.debug("server-path: " + serverPath);
        log.debug("client-path: " + clientPath);

        return Helper.matchUrl(clientPath, serverPath);
    }

    // match class name instead of FQ name
    private boolean isReturnTypeMatched(RestCall restCall, RestEndpoint restEndpoint) {
        if (restCall.isCollection() != restEndpoint.isCollection()) {
            return false;
        }

        String returnTypeA = trimFQName(restCall.getReturnType());
        String returnTypeB = trimFQName(restEndpoint.getReturnType());

        if (isGenericReturnType(returnTypeA) || isGenericReturnType(returnTypeB)) {
            return false;
        } else if (returnTypeA.equals(returnTypeB)) {
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

    private boolean isGenericReturnType(String returnType) {
        // template class, not generic
        if (returnType.endsWith(">")) {
            return false;
        }
        return returnType.contains("Response") || returnType.contains("HttpEntity");
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

