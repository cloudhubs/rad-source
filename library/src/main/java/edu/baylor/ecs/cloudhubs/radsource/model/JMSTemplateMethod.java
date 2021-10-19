package edu.baylor.ecs.cloudhubs.radsource.model;

import lombok.*;

/**
 * author: Abdullah Al Maruf
 * date: 10/18/21
 * time: 4:34 AM
 * website : https://maruftuhin.com
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JMSTemplateMethod {
    private String methodName;

    private static final JMSTemplateMethod[] jmsTemplateMethods = {
            new JMSTemplateMethod("convertAndSend"),
    };

    public static JMSTemplateMethod findByName(String methodName) {
        for (JMSTemplateMethod restTemplateMethod : jmsTemplateMethods) {
            if (restTemplateMethod.methodName.equals(methodName)) {
                return restTemplateMethod;
            }
        }
        return null;
    }
}
