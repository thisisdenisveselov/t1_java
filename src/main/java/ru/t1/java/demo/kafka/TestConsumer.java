package ru.t1.java.demo.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class TestConsumer {

    //@KafkaListener(topics = "test",  groupId = "group_id")
    public void consume(String message) {
        System.out.println("message1: " + message);
    }
}
