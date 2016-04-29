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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.capgemini.gregor.EnableKafkaConsumers;
import com.capgemini.gregor.KafkaConsumer;
import com.capgemini.gregor.PayloadContent;
import com.capgemini.gregor.integration.SingleStringConsumerTest.TestConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@IntegrationTest({"kafka.addresses=localhost:" + BaseKafkaTest.BROKER_PORT,
    "zookeeper.address=localhost:" + BaseKafkaTest.ZOOKEEPER_PORT})
public class SingleStringConsumerTest extends BaseKafkaTest {
    
    public static List<String> receivedMessages = new ArrayList<String>();
    
    @After
    public void reset() {
        receivedMessages.clear();
    }
    
    @Test
    public void testSingleMessage() {
        final String message = "Hey Mr Tambourine Man";
        
        sendMessage(TEST_TOPIC, message);
        
        assertMessageReceived(message, 0);
    }
    
    @Test
    public void testMultipleMessages() {
        final String message1 = "Hey Mr Tambourine Man";
        sendMessage(TEST_TOPIC, message1);        
        assertMessageReceived(message1, 0);
        
        final String message2 = "play a song for me";
        sendMessage(TEST_TOPIC, message2);        
        assertMessageReceived(message2, 1);
        
        final String message3 = "I'm not sleepy and";
        sendMessage(TEST_TOPIC, message3);        
        assertMessageReceived(message3, 2);
        
        final String message4 = "there is no place I'm going to";
        sendMessage(TEST_TOPIC, message4);        
        assertMessageReceived(message4, 3);
    }
    
    @Test
    public void testWrongTopic() {
        final String message = "This should not be received";
        
        sendMessage(TEST_TOPIC2, message);
        
        waitForMessage();
        assertEquals("Message received incorrectly", 0, receivedMessages.size());
    }
    
    private void assertMessageReceived(String expectedMessage, int index) {
        waitForMessage();
        
        assertEquals(expectedMessage, receivedMessages.get(index));
    }
    
    @Configuration
    @EnableKafkaConsumers
    public static class TestConfiguration {

        public TestConfiguration() {
            
        }
        
        @KafkaConsumer(topic = TEST_TOPIC, payloadContent = PayloadContent.STRING)
        public void testConsumer(String value) {
            receivedMessages.add(value);
        }
    }
}
