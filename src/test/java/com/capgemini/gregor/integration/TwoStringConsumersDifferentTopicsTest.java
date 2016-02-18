/*
* Copyright 2015 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.capgemini.gregor.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.gregor.EnableKafkaConsumers;
import com.capgemini.gregor.KafkaConsumer;
import com.capgemini.gregor.PayloadContent;
import com.capgemini.gregor.integration.TwoStringConsumersDifferentTopicsTest.TestConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@IntegrationTest({"kafka.address=localhost:" + BaseKafkaTest.BROKER_PORT, 
    "zookeeper.address=localhost:" + BaseKafkaTest.ZOOKEEPER_PORT})
public class TwoStringConsumersDifferentTopicsTest extends BaseKafkaTest {
    
    public static Map<String, List<String>> receivedMessages = new HashMap<String, List<String>>();
    
    @After
    public void reset() {
        receivedMessages.clear();
    }
    
    @Test
    public void testSingleMessageTopic1() {
        final String message = "Hey Mr Tambourine Man";
        
        sendMessage(TEST_TOPIC, message);
        
        assertMessageReceived(TEST_TOPIC, message, 0);
        assertMessageNotReceived(TEST_TOPIC2, message, 0, false);   
    }
    
    @Test
    public void testSingleMessageTopic2() {
        final String message = "Hey Mr Tambourine Man";
        
        sendMessage(TEST_TOPIC2, message);
        
        assertMessageReceived(TEST_TOPIC2, message, 0);
        assertMessageNotReceived(TEST_TOPIC, message, 0, false);   
    }
    
    @Test
    public void testMultipleMessages() {
        final String message1 = "Hey Mr Tambourine Man";
        sendMessage(TEST_TOPIC, message1);        
        assertMessageReceived(TEST_TOPIC, message1, 0);
        assertMessageNotReceived(TEST_TOPIC2, message1, 0, false);
        
        final String message2 = "play a song for me";
        sendMessage(TEST_TOPIC2, message2);        
        assertMessageReceived(TEST_TOPIC2, message2, 0);
        assertMessageNotReceived(TEST_TOPIC, message2, 1, false);
        
        final String message3 = "I'm not sleepy and";
        sendMessage(TEST_TOPIC, message3);        
        assertMessageReceived(TEST_TOPIC, message3, 1);
        assertMessageNotReceived(TEST_TOPIC2, message3, 1, false);
        
        final String message4 = "there is no place I'm going to";
        sendMessage(TEST_TOPIC2, message4);        
        assertMessageReceived(TEST_TOPIC2, message4, 1);
        assertMessageNotReceived(TEST_TOPIC, message4, 2, false);
    }
    
    @Test
    public void testWrongTopic() {
        final String message = "This should not be received";
        
        sendMessage(TEST_TOPIC3, message);
        
        assertMessageNotReceived(TEST_TOPIC, message, 0, true);
        assertMessageNotReceived(TEST_TOPIC2, message, 0, false);
    }
    
    private void waitForMessage() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void assertMessageReceived(String topic, String expectedMessage, int index) {
        waitForMessage();
        
        assertEquals(expectedMessage, receivedMessages.get(topic).get(index));
    }
    
    private void assertMessageNotReceived(String topic, String message, int index, boolean wait) {
       
        if (wait) {
            waitForMessage();
        }
        
        if (receivedMessages.containsKey(topic)) {
            final List<String> messageList = receivedMessages.get(topic);
            
            if (messageList.size() > index) {
                assertNotEquals("Message received incorrectly", message, messageList.get(index));
            }
        }
    }
    
    @Configuration
    @EnableKafkaConsumers
    public static class TestConfiguration {

        public TestConfiguration() {
            
        }
        
        @KafkaConsumer(topic = TEST_TOPIC, payloadContent = PayloadContent.STRING)
        public void testConsumer(String value) {
            onMessageReceived(TEST_TOPIC, value);
        }
        
        @KafkaConsumer(topic = TEST_TOPIC2, payloadContent = PayloadContent.STRING)
        public void testAnotherConsumer(String value) {
            onMessageReceived(TEST_TOPIC2, value);
        }
        
        private void onMessageReceived(String topic, String message) {
            if (!receivedMessages.containsKey(topic)) {
                receivedMessages.put(topic, new ArrayList<String>());
            }
            
            receivedMessages.get(topic).add(message);
        }
    }
}
