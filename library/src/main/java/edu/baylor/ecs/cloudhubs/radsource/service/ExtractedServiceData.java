package edu.baylor.ecs.cloudhubs.radsource.service;

import lombok.*;
import java.util.List;

//@Getter
//@Setter
//@ToString
//@AllArgsConstructor
//@NoArgsConstructor
//public class ExtractedJsonData {
//    private List<ServiceData> services;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExtractedServiceData {
    private String name;
    private List<EndpointData> endpoints;
    private List<EventData> events;
    private List<GRPCData> grpcList;
    private CallData callsData;
    
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EndpointData {
        private String name;
        private String arguments;
        private String methodType;
        private String returnType;
        private String path;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventData {
        private String name;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GRPCData {
        private String name;
        private String arguments;
        private String methodType;
        private String returnType;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CallData {
    	private List<EndpointData> endpoints;
        private List<EventData> events;
        private List<GRPCData> grpcList;
    }
}

    
//}