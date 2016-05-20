package com.capgemini.gregor.integration;

import com.capgemini.gregor.EnableKafkaProducers;
import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaProducer;
import com.capgemini.gregor.TestObject;
import com.capgemini.gregor.integration.SimpleJSONProducerTest.TestConfiguration;
import com.capgemini.gregor.integration.SimpleJSONProducerTest.TestConfiguration.TestProducer;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@IntegrationTest({"kafka.addresses=localhost:" + BaseKafkaTest.BROKER_PORT,
    "zookeeper.address=localhost:" + BaseKafkaTest.ZOOKEEPER_PORT})
public class SimpleJSONProducerTest extends BaseKafkaTest {

    @Autowired
    private TestProducer testProducer;

    @Test
    public void testSingleMessageUsingFirstIntefaceMethodSentSuccessfully() throws TimeoutException {

        final TestObject objectToSend = createTestObject();

        testProducer.sendMessageOne(objectToSend);

        final List<String> receivedMessages = readMessages(TEST_TOPIC, 1);
        final TestObject receivedObject = new Gson().fromJson(receivedMessages.get(0), TestObject.class);
        assertEquals("Message not received", 1, receivedMessages.size());
        assertEquals("Received message is not correct", objectToSend, receivedObject);
    }

    @Test
    public void testSingleMessageUsingFirstIntefaceMethodNotSentToOtherTopic() throws TimeoutException {

        final TestObject objectToSend = createTestObject();
        testProducer.sendMessageOne(objectToSend);

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

        final TestObject objectToSend = createTestObject();
        testProducer.sendMessageTwo(objectToSend);

        final List<String> receivedMessages = readMessages(TEST_TOPIC2, 1);
        final TestObject receivedObject = new Gson().fromJson(receivedMessages.get(0), TestObject.class);
        assertEquals("Message not received", 1, receivedMessages.size());
        assertEquals("Received message is not correct", objectToSend, receivedObject);
    }

    @Test
    public void testSingleMessageUsingSecondIntefaceMethodNotSentToOtherTopic() throws TimeoutException {

        final TestObject objectToSend = createTestObject();
        testProducer.sendMessageTwo(objectToSend);

        boolean testPass = false;

        try {
            readMessages(TEST_TOPIC, 1);
        } catch (TimeoutException e) {
            //The test expects that an exception should be thrown as a message should not be received
            testPass = true;
        }

        assertEquals("Message receieved when it should not have been", true, testPass);
    }

    private TestObject createTestObject() {
        final TestObject objectToSend = new TestObject();
        objectToSend.setBooleanValue(true);
        objectToSend.setIntValue(123);
        objectToSend.setStringValue("This Is A String");
        objectToSend.setListValue(Arrays.asList(new String[] {"One", "Two", "Three"}));
        final Map<String, String> map = new HashMap<String, String>();
        map.put("Key", "Value");
        objectToSend.setMapValue(map);

        return objectToSend;
    }

    @EnableKafkaProducers
    public static class TestConfiguration {

        public TestConfiguration() {

        }

        @KafkaClient
        public interface TestProducer {

            @KafkaProducer(topic = TEST_TOPIC)
            void sendMessageOne(TestObject message);

            @KafkaProducer(topic = TEST_TOPIC2)
            void sendMessageTwo(TestObject message);
        }
    }
}
