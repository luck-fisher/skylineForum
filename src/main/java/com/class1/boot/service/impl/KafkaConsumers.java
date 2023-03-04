package com.class1.boot.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumers {
    @KafkaListener(topics = {"test"})
    public void getMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}