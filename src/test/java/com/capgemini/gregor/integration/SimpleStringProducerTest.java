package com.capgemini.gregor.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.gregor.EnableKafkaProducers;
import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaProducer;
import com.capgemini.gregor.integration.SimpleStringProducerTest.TestConfiguration;
import com.capgemini.gregor.integration.SimpleStringProducerTest.TestConfiguration.TestProducer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@IntegrationTest({"kafka.addresses=localhost:" + BaseKafkaTest.BROKER_PORT,
    "zookeeper.address=localhost:" + BaseKafkaTest.ZOOKEEPER_PORT})
public class SimpleStringProducerTest extends BaseKafkaTest {

    private static final String MESSAGE_TEXT = "Testing Testing 123";

    @Autowired
    private TestProducer testProducer;
    
    @Test
    public void testSingleMessageUsingFirstIntefaceMethodSentSuccessfully() throws TimeoutException {

        testProducer.sendMessageOne(MESSAGE_TEXT);

        final List<String> receivedMessages = readMessages(TEST_TOPIC, 1);
        assertEquals("Message not received", 1, receivedMessages.size());
        assertEquals("Received message is not correct", MESSAGE_TEXT, receivedMessages.get(0));
    }

    @Test
    public void testSingleMessageUsingFirstIntefaceMethodNotSentToOtherTopic() throws TimeoutException {

        testProducer.sendMessageOne(MESSAGE_TEXT);

        boolean testPass = false;

        try {
            readMessages(TEST_TOPIC2, 1);
        } catch (TimeoutException e) {
            //The test expects that an exception should be thrown as a message should not be received
            testPass = true;
        }

        assertEquals("Message receieved when it should not have been", true, testPass);
    }

    @Test
    public void testSingleMessageUsingSecondIntefaceMethodSentSuccessfully() throws TimeoutException {

        testProducer.sendMessageTwo(MESSAGE_TEXT);

        final List<String> receivedMessages = readMessages(TEST_TOPIC2, 1);
        assertEquals("Message not received", 1, receivedMessages.size());
        assertEquals("Received message is not correct", MESSAGE_TEXT, receivedMessages.get(0));
    }

    @Test
    public void testSingleMessageUsingSecondIntefaceMethodNotSentToOtherTopic() throws TimeoutException {

        testProducer.sendMessageTwo(MESSAGE_TEXT);

        boolean testPass = false;

        try {
            readMessages(TEST_TOPIC, 1);
        } catch (TimeoutException e) {
            //The test expects that an exception should be thrown as a message should not be received
            testPass = true;
        }

        assertEquals("Message receieved when it should not have been", true, testPass);
    }
    
    @Configuration
    @EnableKafkaProducers
    public static class TestConfiguration {

        public TestConfiguration() {
            
        }
        
        @KafkaClient
        public interface TestProducer {
            
            @KafkaProducer(topic = TEST_TOPIC)
            void sendMessageOne(String message);
            
            @KafkaProducer(topic = TEST_TOPIC2)
            void sendMessageTwo(String message);
        }
    }
}
