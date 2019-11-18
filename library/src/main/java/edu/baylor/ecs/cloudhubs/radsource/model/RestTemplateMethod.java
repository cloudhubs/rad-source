package edu.baylor.ecs.cloudhubs.radsource.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RestTemplateMethod {
    private String methodName;
    private HttpMethod httpMethod;
    private int numberOfParams;

    private static final RestTemplateMethod[] restTemplateMethods = {
            new RestTemplateMethod("getForObject", HttpMethod.GET, 2),
            new RestTemplateMethod("getForEntity", HttpMethod.GET, 2),
            new RestTemplateMethod("exchange", HttpMethod.GET, 3),
            new RestTemplateMethod("postForObject", HttpMethod.POST, 3),
            new RestTemplateMethod("delete", HttpMethod.DELETE, 1),
            new RestTemplateMethod("put", HttpMethod.PUT, 2),
    };

    public static RestTemplateMethod findByName(String methodName) {
        for (RestTemplateMethod restTemplateMethod : restTemplateMethods) {
            if (restTemplateMethod.methodName.equals(methodName)) {
                return restTemplateMethod;
            }
        }
        return null;
    }
}
