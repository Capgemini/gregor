package com.capgemini.gregor;

import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * A callback for notification of a success or failure when producing a messge.
 */
public interface KafkaProducerCallback {
    void onSuccess(RecordMetadata metadata);

    void onFailure(Throwable cause);
}
