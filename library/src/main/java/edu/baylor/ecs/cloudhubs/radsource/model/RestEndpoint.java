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
    private String path;
    private boolean isCollection;

    public RestEndpoint(String httpMethod, String parentMethod, String arguments, String returnType) {
        this.httpMethod = httpMethod;
        this.parentMethod = parentMethod;
        this.arguments = arguments;
        this.returnType = returnType;
    }
}
