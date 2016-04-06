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
import java.util.Arrays;
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
import com.capgemini.gregor.TestObject;
import com.capgemini.gregor.integration.SingleJSONConsumerTest.TestConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@IntegrationTest({"kafka.address=localhost:" + BaseKafkaTest.BROKER_PORT, 
    "zookeeper.address=localhost:" + BaseKafkaTest.ZOOKEEPER_PORT})
public class SingleJSONConsumerTest extends BaseKafkaTest {
    
    public static List<TestObject> receivedMessages = new ArrayList<TestObject>();
    
    private static final String TEST_OBJECT_JSON = "{\"stringValue\" : \"Test String\", \"intValue\" : 1234, \"booleanValue\" : true, \"listValue\" : [\"One\", \"Two\", \"Three\"], \"mapValue\" : {\"Key1\" : \"Value1\", \"Key2\" : \"Value2\"}}";
    
    @After
    public void reset() {
        receivedMessages.clear();
    }
    
    @Test
    public void testSingleMessage() {        
        sendMessage(TEST_TOPIC, TEST_OBJECT_JSON);
        
        waitForMessage();
        
        assertEquals("The message was not received!", 1, receivedMessages.size());
        assertThatReceivedTestObjectIsCorrect();

    }
    
    private void assertThatReceivedTestObjectIsCorrect() {
        final TestObject testObject = receivedMessages.get(0);
        
        assertEquals("stringValue not correct", "Test String", testObject.getStringValue());
        assertEquals("intValue not correct", 1234, testObject.getIntValue());
        assertEquals("booleanValue not correct", true, testObject.isBooleanValue());
        assertEquals("listValue not correct", true, Arrays.equals(testObject.getListValue().toArray(), new String[]{"One", "Two", "Three"}));
        assertEquals("mapValue keys incorrect", true, Arrays.equals(testObject.getMapValue().keySet().toArray(), new String[]{"Key1", "Key2"}));
        assertEquals("mapValue values incorrect", true, Arrays.equals(testObject.getMapValue().values().toArray(), new String[]{"Value1", "Value2"}));
    }
    
    @Test
    public void testWrongTopic() {
        final String message = "This should not be received";
        
        sendMessage(TEST_TOPIC2, message);
        
        waitForMessage();
        assertEquals("Message received incorrectly", 0, receivedMessages.size());
    }
    
    @Configuration
    @EnableKafkaConsumers
    public static class TestConfiguration {

        public TestConfiguration() {
            
        }
        
        @KafkaConsumer(topic = TEST_TOPIC)
        public void testConsumer(TestObject value) {
            receivedMessages.add(value);
        }
    }
}
