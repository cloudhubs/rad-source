package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RadSourceServiceTest {

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
    }
}