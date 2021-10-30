package edu.baylor.ecs.cloudhubs.radsource.service;

import edu.baylor.ecs.cloudhubs.radsource.context.MQEntityContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
import edu.baylor.ecs.cloudhubs.radsource.context.RestEntityContext;
import edu.baylor.ecs.cloudhubs.radsource.graph.GVGenerator;
import edu.baylor.ecs.cloudhubs.radsource.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class RadSourceService {
    private final RestCallService restCallService;
    private final RestEndpointService restEndpointService;
    private final RestFlowService restFlowService;
    private final MQConsumerService mqConsumerService;
    private final MQProducerService mqProducerService;
    private final MQFlowService mqFlowService;

    // no args constructor
    // initialize restCallService manually
    public RadSourceService() {
        this.restCallService = new RestCallService();
        this.restEndpointService = new RestEndpointService();
        this.restFlowService = new RestFlowService();

        this.mqConsumerService = new MQConsumerService();
        this.mqProducerService = new MQProducerService();
        this.mqFlowService = new MQFlowService();
    }

    public RadSourceResponseContext generateRadSourceResponseContext(RadSourceRequestContext request) throws IOException {
        RadSourceResponseContext responseContext = new RadSourceResponseContext();
        responseContext.setRequest(request);

        List<RestEntityContext> restEntityContexts = new ArrayList<>();
        List<MQEntityContext> mqEntityContexts = new ArrayList<>();

        for (String pathToMsRoot : request.getPathToMsRoots()) {
            restEntityContexts.add(generateRestEntityContext(pathToMsRoot));
            mqEntityContexts.add(generateMQEntityContext(pathToMsRoot));
        }

        List<RestFlow> restFlows = restFlowService.findRestFlows(restEntityContexts);
        List<MQFlow> mqFlows = mqFlowService.findMQFlowService(mqEntityContexts);

        responseContext.setMqEntityContexts(mqEntityContexts);
        responseContext.setRestEntityContexts(restEntityContexts);
        responseContext.setRestFlows(restFlows);
        responseContext.setMqFlows(mqFlows);

        GVGenerator.generate(responseContext);

        return responseContext;
    }

    private RestEntityContext generateRestEntityContext(String pathToMsRoot) throws IOException {
        RestEntityContext restEntityContext = new RestEntityContext();
        restEntityContext.setPathToMsRoot(pathToMsRoot);

        List<RestCall> restCalls = new ArrayList<>();
        List<RestEndpoint> restEndpoints = new ArrayList<>();

        for (File sourceFile : getSourceFiles(pathToMsRoot)) {
            restCalls.addAll(restCallService.findRestCalls(sourceFile));
            restEndpoints.addAll(restEndpointService.findRestEndpoints(sourceFile));
        }

        // add msRoot to all restCalls and restEndpoints
        restCalls.forEach(e -> e.setMsRoot(pathToMsRoot));
        restEndpoints.forEach(e -> e.setMsRoot(pathToMsRoot));

        restEntityContext.setRestCalls(restCalls);
        restEntityContext.setRestEndpoints(restEndpoints);

        return restEntityContext;
    }

    // TODO
    private MQEntityContext generateMQEntityContext(String pathToMsRoot) throws IOException {
        MQEntityContext mqEntityContext = new MQEntityContext();
        mqEntityContext.setPathToMsRoot(pathToMsRoot);

        List<MessageQueue> consumer = new ArrayList<>();
        List<MessageQueue> producer = new ArrayList<>();

        for (File sourceFile : getSourceFiles(pathToMsRoot)) {
            consumer.addAll(mqConsumerService.findConsumers(sourceFile));
            producer.addAll(mqProducerService.findProducers(sourceFile));
        }

        // add msRoot to all restCalls and restEndpoints
        consumer.forEach(e -> e.setMsRoot(pathToMsRoot));
        producer.forEach(e -> e.setMsRoot(pathToMsRoot));

        mqEntityContext.setConsumers(consumer);
        mqEntityContext.setProducers(producer);

        return mqEntityContext;
    }

    private List<File> getSourceFiles(String directoryOrFile) {
        if (directoryOrFile.endsWith(".java")) { // not a directory, but a single java file
            return new ArrayList<>(Collections.singletonList(new File(directoryOrFile)));
        } else {
            return (List<File>) FileUtils.listFiles(new File(directoryOrFile), new String[]{"java"}, true);
        }
    }
}
