package com.capgemini.gregor.internal.producer.client;

import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaProducer;

@KafkaClient
public interface SimpleKafkaClient {

    @KafkaProducer(topic = "testTopic")
    void produce(String arg);
}
