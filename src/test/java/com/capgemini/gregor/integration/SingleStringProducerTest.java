package com.capgemini.gregor.integration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.Producer;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.gregor.EnableKafkaConsumers;
import com.capgemini.gregor.EnableKafkaProducers;
import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaConsumer;
import com.capgemini.gregor.KafkaProducer;
import com.capgemini.gregor.PayloadContent;
import com.capgemini.gregor.integration.SingleStringProducerTest.TestConfiguration;
import com.capgemini.gregor.integration.SingleStringProducerTest.TestConfiguration.TestProducer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@IntegrationTest({"kafka.addresses=localhost:" + BaseKafkaTest.BROKER_PORT,
    "zookeeper.address=localhost:" + BaseKafkaTest.ZOOKEEPER_PORT})
public class SingleStringProducerTest extends BaseKafkaTest {
    
    @Autowired
    private TestProducer testProducer;
    
    @Test
    public void testSendingSingleMessage() throws TimeoutException {
        final String messageText = "Testing Testing 123";
        
        testProducer.sendMessage(messageText);
        waitForMessage();

        final List<String> receivedMessages = readMessages(TEST_TOPIC, 1);
        assertEquals("Message not received", 1, receivedMessages.size());
        assertEquals("Received message is not correct", messageText, receivedMessages.get(0));
    }
    
    @Configuration
    @EnableKafkaProducers
    public static class TestConfiguration {

        public TestConfiguration() {
            
        }
        
        @KafkaClient
        public interface TestProducer {
            
            @KafkaProducer(topic = TEST_TOPIC)
            public void sendMessage(String message);
            
            @KafkaProducer(topic = TEST_TOPIC + 1)
            public void sendMessageToo(String message);
        }
    }
}
