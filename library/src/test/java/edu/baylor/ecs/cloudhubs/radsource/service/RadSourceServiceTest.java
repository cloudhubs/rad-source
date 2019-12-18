package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import edu.baylor.ecs.cloudhubs.radsource.model.RestCall;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RadSourceServiceTest {

    private List<RestCall> expectedRestCalls = Arrays.asList(
            new RestCall(null, null, "GET", "doGetForObject", "SampleModel", false),
            new RestCall(null, null, "GET", "doGetForEntity", "SampleModel", false),
            new RestCall(null, null, "GET", "doExchangeGet", "SampleModel", false),
            new RestCall(null, null, "POST", "doPostForObject", "SampleModel", false),
            new RestCall(null, null, "POST", "doPostForEntity", "SampleModel", false),
            new RestCall(null, null, "POST", "doExchangePost", "SampleModel", false)
    );

    @Test
    void generateRadSourceResponseContext() throws IOException {
        RadSourceService radSourceService = new RadSourceService();

        List<String> paths = new ArrayList<>();
        paths.add("../sample");

        RadSourceRequestContext radSourceRequestContext = new RadSourceRequestContext(paths);
        RadSourceResponseContext radSourceResponseContext = radSourceService.generateRadSourceResponseContext(radSourceRequestContext);

        assertEquals(radSourceResponseContext.getRestEntityContexts().size(), 1);
        assertEquals(radSourceResponseContext.getRestEntityContexts().get(0).getRestCalls().size(), 6);
        assertEquals(radSourceResponseContext.getRestEntityContexts().get(0).getRestEndpoints().size(), 4);

        for (RestCall restCall : radSourceResponseContext.getRestEntityContexts().get(0).getRestCalls()) {
            for (RestCall expectedRestCall : expectedRestCalls) {
                if (restCall.getParentMethod().contains(expectedRestCall.getParentMethod())) {
                    assertEquals(restCall.getHttpMethod(), expectedRestCall.getHttpMethod());
                    assertEquals(restCall.getReturnType(), expectedRestCall.getReturnType());
                    assertEquals(restCall.isCollection(), expectedRestCall.isCollection());
                }
            }
        }
    }
}