package edu.baylor.ecs.cloudhubs.radsource.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestCall {
    private String httpMethod;
    private String parentMethod;
    private String returnType;
    private boolean isCollection;
}
