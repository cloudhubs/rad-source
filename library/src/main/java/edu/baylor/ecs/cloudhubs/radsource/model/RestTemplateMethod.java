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
    private int responseTypeIndex;

    private static final RestTemplateMethod[] restTemplateMethods = {
            new RestTemplateMethod("getForObject", HttpMethod.GET, 1),
            new RestTemplateMethod("getForEntity", HttpMethod.GET, 1),
            new RestTemplateMethod("postForObject", HttpMethod.POST, 2),
            new RestTemplateMethod("put", HttpMethod.PUT, 1),
            new RestTemplateMethod("exchange", HttpMethod.GET, 3),
            new RestTemplateMethod("delete", HttpMethod.DELETE, 0), // TODO: delete doesn't work
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
