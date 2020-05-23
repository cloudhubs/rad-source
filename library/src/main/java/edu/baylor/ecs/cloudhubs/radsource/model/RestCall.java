package edu.baylor.ecs.cloudhubs.radsource.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RestCall {
    private String msRoot;
    private String source;
    private String httpMethod;
    private String parentMethod;
    private String returnType;
    private String url;
    private boolean isCollection;

    public RestCall(String httpMethod, String parentMethod, String returnType) {
        this.httpMethod = httpMethod;
        this.parentMethod = parentMethod;
        this.returnType = returnType;
    }
}
