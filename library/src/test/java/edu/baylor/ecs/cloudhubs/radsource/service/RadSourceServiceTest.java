package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import edu.baylor.ecs.cloudhubs.radsource.model.RestCall;
import edu.baylor.ecs.cloudhubs.radsource.model.RestEndpoint;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RadSourceServiceTest {

    private List<RestEndpoint> expectedRestEndpoints = Arrays.asList(
            new RestEndpoint("GET", "doGetMapping", "[]", "SampleModel"),
            new RestEndpoint("GET", "doRequestMappingGet", "[]", "SampleModel"),
            new RestEndpoint("POST", "doPostMapping", "[@RequestBody SampleModel sampleModel]", "SampleModel"),
            new RestEndpoint("POST", "doRequestMappingPost", "[@RequestBody SampleModel sampleModel]", "SampleModel")
    );

    private List<RestCall> expectedRestCalls = Arrays.asList(
            new RestCall("GET", "doGetForObject", "SampleModel"),
            new RestCall("GET", "doGetForEntity", "SampleModel"),
            new RestCall("GET", "doExchangeGet", "SampleModel"),
            new RestCall("POST", "doPostForObject", "SampleModel"),
            new RestCall("POST", "doPostForEntity", "SampleModel"),
            new RestCall("POST", "doExchangePost", "SampleModel")
    );

    @Test
    void generateRadSourceResponseContext() throws IOException {
        RadSourceService radSourceService = new RadSourceService();

        List<String> paths = new ArrayList<>();
        paths.add("../sample");

        RadSourceRequestContext radSourceRequestContext = new RadSourceRequestContext(paths, null);
        RadSourceResponseContext radSourceResponseContext = radSourceService.generateRadSourceResponseContext(radSourceRequestContext);

        assertEquals(radSourceResponseContext.getRestEntityContexts().size(), 1);
        assertEquals(radSourceResponseContext.getRestEntityContexts().get(0).getRestCalls().size(), 6);
        assertEquals(radSourceResponseContext.getRestEntityContexts().get(0).getRestEndpoints().size(), 4);

        for (RestEndpoint restEndpoint : radSourceResponseContext.getRestEntityContexts().get(0).getRestEndpoints()) {
            for (RestEndpoint expectedRestEndpoint : expectedRestEndpoints) {
                if (restEndpoint.getParentMethod().contains(expectedRestEndpoint.getParentMethod())) {
                    assertEquals(restEndpoint.getHttpMethod(), expectedRestEndpoint.getHttpMethod());
                    assertEquals(restEndpoint.getReturnType(), expectedRestEndpoint.getReturnType());
                    assertEquals(restEndpoint.isCollection(), expectedRestEndpoint.isCollection());
                }
            }
        }

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