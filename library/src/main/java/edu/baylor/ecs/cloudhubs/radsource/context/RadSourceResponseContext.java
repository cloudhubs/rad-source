package edu.baylor.ecs.cloudhubs.radsource.context;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RadSourceResponseContext {
    private RadSourceRequestContext request;
}