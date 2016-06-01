package com.capgemini.gregor.integration;

import com.capgemini.gregor.EnableKafkaProducers;
import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaProducer;
import com.capgemini.gregor.KafkaProducerCallback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KafkaProducerCallbackTest.TestConfiguration.class})
@IntegrationTest({"kafka.addresses=localhost:" + BaseKafkaTest.BROKER_PORT,
    "zookeeper.address=localhost:" + BaseKafkaTest.ZOOKEEPER_PORT})
public class KafkaProducerCallbackTest extends BaseKafkaTest {

    private static final String MESSAGE_TEXT = "Testing Testing 123";

    @Autowired
    private TestConfiguration.TestProducer testProducer;

    private boolean onSuccessTriggered = false;

    private boolean onFailureTriggered = false;

    @After
    public void reset() {
        onSuccessTriggered = false;
        onFailureTriggered = false;
    }
    
    @Test
    public void testOnSuccessTriggered() throws TimeoutException {

        testProducer.sendMessage(MESSAGE_TEXT, new KafkaProducerCallback(){

            @Override
            public void onSuccess(RecordMetadata metadata) {
                onSuccessTriggered = true;
            }

            @Override
            public void onFailure(Throwable cause) {
                onFailureTriggered = true;
            }
        });

        final List<String> receivedMessages = readMessages(TEST_TOPIC, 1);
        assertEquals("Message not received", 1, receivedMessages.size());
        assertEquals("onSuccess not triggered", true, onSuccessTriggered);
        assertEquals("onFailure triggered incorrectly", false, onFailureTriggered);
    }
    
    @Configuration
    @EnableKafkaProducers
    public static class TestConfiguration {

        public TestConfiguration() {
            
        }
        
        @KafkaClient
        public interface TestProducer {
            
            @KafkaProducer(topic = TEST_TOPIC, payloadSerializer = StringSerializer.class)
            void sendMessage(String message, KafkaProducerCallback callback);
        }
    }
}
