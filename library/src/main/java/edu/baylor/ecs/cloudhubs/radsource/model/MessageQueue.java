package edu.baylor.ecs.cloudhubs.radsource.model;

/**
 * author: Abdullah Al Maruf
 * date: 10/15/21
 * time: 3:39 AM
 * website : https://maruftuhin.com
 */

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageQueue {
    private String msRoot;
    private String source;
    // jmstemplate, rabbittemplate, redistemplate, etc
    private String driverClass;
    private String server;
    private String queueName;
    private String parentMethod;
    private boolean isCollection;

    public MessageQueue(String queueName, String parentMethod, String returnType) {
        this.queueName = queueName;
        this.parentMethod = parentMethod;
    }
}
