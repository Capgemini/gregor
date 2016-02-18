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

package com.capgemini.gregor.internal.consumer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

import com.capgemini.gregor.TestObject;

public class ReflectiveDelegatingMessageHandlerTest {

    private ReflectiveDelegatingMessageHandler underTest;
    
    private boolean delegateCalled = false;
    
    private TestObject objectReceivedByDelegate;
    
    @After
    public void reset() {
        delegateCalled = false;
        objectReceivedByDelegate = null;
    }
    
    @Test
    public void testHandleMessageHappyPath() throws NoSuchMethodException, SecurityException {
        underTest = new ReflectiveDelegatingMessageHandler(
                new TestDelegate(), "delegateMethod", TestObject.class);
        
        final TestObject payload = new TestObject();
        
        underTest.handleMessage(createMessage(payload));
        
        assertTrue("Delegate method not called", delegateCalled);
        assertEquals("Incorrect object passed to delegate", payload, objectReceivedByDelegate);
    }
    
    @Test(expected = MessagingException.class)
    public void testHandleMessageIncorrectPayloadType() throws NoSuchMethodException, SecurityException {
        underTest = new ReflectiveDelegatingMessageHandler(
                new TestDelegate(), "delegateMethod", TestObject.class);
        
        final String payload = new String();
        
        underTest.handleMessage(createMessage(payload));
    }
    
    private <T> Message<T> createMessage(T payload) {
        return new GenericMessage<T>(payload);
    }
    
    private class TestDelegate {
        
        public void delegateMethod(TestObject arg) {
            delegateCalled = true;
            objectReceivedByDelegate = arg;
        }
    }
}
