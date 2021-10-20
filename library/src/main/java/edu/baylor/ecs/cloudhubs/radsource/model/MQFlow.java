package edu.baylor.ecs.cloudhubs.radsource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Abdullah Al Maruf
 * date: 10/15/21
 * time: 3:47 AM
 * website : https://maruftuhin.com
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MQFlow {
    private MessageQueue consumer;
    private MessageQueue producer;
}
