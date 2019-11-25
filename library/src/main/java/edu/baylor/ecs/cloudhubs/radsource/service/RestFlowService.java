package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.RestEntityContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RestFlow;

import java.util.ArrayList;
import java.util.List;

public class RestFlowService {
    public List<RestFlow> findRestFlows(List<RestEntityContext> restEntityContexts) {
        List<RestFlow> restFlows = new ArrayList<>();

        for (RestEntityContext contextA : restEntityContexts) {
            for (RestEntityContext contextB : restEntityContexts) {
                // don't match same MS
                if (contextA.getPathToMsRoot().equals(contextB.getPathToMsRoot())) continue;

                // TODO
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
}
