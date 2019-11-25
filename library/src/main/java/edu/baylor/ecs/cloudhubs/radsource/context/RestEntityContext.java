package edu.baylor.ecs.cloudhubs.radsource.context;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestEntityContext {
    String pathToMsRoot;
    private List<RestCall> restCalls;
    private List<RestEndpoint> restEndpoints;
}
