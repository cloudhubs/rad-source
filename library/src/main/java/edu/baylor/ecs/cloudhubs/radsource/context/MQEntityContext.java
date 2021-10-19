package edu.baylor.ecs.cloudhubs.radsource.context;

import edu.baylor.ecs.cloudhubs.radsource.model.MessageQueue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MQEntityContext {
    String pathToMsRoot;
    private List<MessageQueue> consumers;
    private List<MessageQueue> producers;
}
