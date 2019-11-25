package edu.baylor.ecs.cloudhubs.radsource.context;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RadSourceResponseContext {
    private RadSourceRequestContext request;
    private List<RestEntityContext> restEntityContexts;
    private List<RestFlow> restFlows;
}