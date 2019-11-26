package edu.baylor.ecs.cloudhubs.radsource.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RestEndpoint {
    private String msRoot;
    private String source;
    private String httpMethod;
    private String parentMethod;
    private String arguments;
    private String returnType;
    private boolean isCollection;
}
