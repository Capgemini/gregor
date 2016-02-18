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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Factory for creating a channel bean.
 * 
 * @author craigwilliams84
 *
 */
public class ChannelFactory implements FactoryBean<PublishSubscribeChannel> {

    private MessageHandler handler;
    
    @Override
    public PublishSubscribeChannel getObject() throws Exception {
        final PublishSubscribeChannel channel = new PublishSubscribeChannel();
        
        channel.subscribe(getMessageHandler());
        
        return channel;
    }

    @Override
    public Class<PublishSubscribeChannel> getObjectType() {
        return PublishSubscribeChannel.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
    public void setMessageHandler(MessageHandler handler) {
        this.handler = handler;
    }

    public MessageHandler getMessageHandler() {
        return handler;
    }
}
