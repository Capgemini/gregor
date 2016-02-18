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
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageHandler;

public class ChannelFactoryTest {

    private ChannelFactory underTest;
    
    @Before
    public void init() {
        underTest = new ChannelFactory();
    }
    
    @Test
    public void testGetObjectHappyPath() throws Exception {
        final MessageHandler handler = createHandler();
        
        underTest.setMessageHandler(handler);
        PublishSubscribeChannel channel = underTest.getObject();
        
        //Unsubscribe will return true if handler had previously subscribed
        assertTrue("Handler didn't subscribe to channel correctly", channel.unsubscribe(handler));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetObjectHandlerNotSet() throws Exception {       
        PublishSubscribeChannel channel = underTest.getObject();
    }
    
    @Test
    public void testGetObjectType() {
        assertEquals("Incorrect object type returned", PublishSubscribeChannel.class, underTest.getObjectType());
    }
    
    public MessageHandler createHandler() {
        return mock(MessageHandler.class);
    }
}
