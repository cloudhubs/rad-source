//package edu.baylor.ecs.cloudhubs.radsource.service;
//
//import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceRequestContext;
//import edu.baylor.ecs.cloudhubs.radsource.context.RadSourceResponseContext;
//import edu.baylor.ecs.cloudhubs.radsource.context.RestEntityContext;
//import edu.baylor.ecs.cloudhubs.radsource.model.RestCall;
//import edu.baylor.ecs.cloudhubs.radsource.model.RestEndpoint;
//import edu.baylor.ecs.cloudhubs.radsource.model.RestFlow;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.FileUtils;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//@Data
//@Service
//@Slf4j
//public class MQService {
//    private final MQConsumerService mqConsumerService;
//    private final MQProducerService mqProducerService;
//    private final MQFlowService mqFlowService;
//
//    // no args constructor
//    // initialize restCallService manually
//    public MQService() {
//        this.mqConsumerService = new MQConsumerService();
//        this.mqProducerService = new MQProducerService();
//        this.mqFlowService = new MQFlowService();
//    }
//
//    public RadSourceResponseContext generateRadSourceResponseContext(RadSourceRequestContext request) throws IOException {
//        RadSourceResponseContext responseContext = new RadSourceResponseContext();
//        responseContext.setRequest(request);
//
//        List<RestEntityContext> restEntityContexts = new ArrayList<>();
//
//        for (String pathToMsRoot : request.getPathToMsRoots()) {
//            restEntityContexts.add(generateRestEntityContext(pathToMsRoot));
//        }
//
//        List<RestFlow> restFlows = mqFlowService.findRestFlows(restEntityContexts);
//
//        responseContext.setRestEntityContexts(restEntityContexts);
//        responseContext.setRestFlows(restFlows);
//
//        return responseContext;
//    }
//
//    private RestEntityContext generateRestEntityContext(String pathToMsRoot) throws IOException {
//        RestEntityContext restEntityContext = new RestEntityContext();
//        restEntityContext.setPathToMsRoot(pathToMsRoot);
//
//        List<RestCall> restCalls = new ArrayList<>();
//        List<RestEndpoint> restEndpoints = new ArrayList<>();
//
//        for (File sourceFile : getSourceFiles(pathToMsRoot)) {
//            restCalls.addAll(mqConsumerService.findRestCalls(sourceFile));
//            restEndpoints.addAll(mqProducerService.findRestEndpoints(sourceFile));
//        }
//
//        // add msRoot to all restCalls and restEndpoints
//        restCalls.forEach(e -> e.setMsRoot(pathToMsRoot));
//        restEndpoints.forEach(e -> e.setMsRoot(pathToMsRoot));
//
//        restEntityContext.setRestCalls(restCalls);
//        restEntityContext.setRestEndpoints(restEndpoints);
//
//        return restEntityContext;
//    }
//
//    private List<File> getSourceFiles(String directoryOrFile) {
//        if (directoryOrFile.endsWith(".java")) { // not a directory, but a single java file
//            return new ArrayList<>(Collections.singletonList(new File(directoryOrFile)));
//        } else {
//            return (List<File>) FileUtils.listFiles(new File(directoryOrFile), new String[]{"java"}, true);
//        }
//    }
//}
//
