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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.listener.KafkaMessageListenerContainer;

import com.capgemini.gregor.GregorException;
import com.capgemini.gregor.TypeSettableDecoder;

/**
 * Singleton KafkaConsumerBeanDefinitionFactory
 * @author craigwilliams84
 *
 */
public class DefaultKafkaConsumerBeanDefinitionFactory implements KafkaConsumerBeanDefinitionFactory {

    private static DefaultKafkaConsumerBeanDefinitionFactory INSTANCE;
    
    private static final String BEAN_NAME_PREFIX = "gregor-";
    
    private AtomicInteger containerCount = new AtomicInteger(0);
    
    private DefaultKafkaConsumerBeanDefinitionFactory() {
        //Private singleton constructor
    }
    
    public static DefaultKafkaConsumerBeanDefinitionFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultKafkaConsumerBeanDefinitionFactory();
        }
        
        return INSTANCE;
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<BeanDefinitionHolder> create(String topicName, ConsumerDetails consumerDetails) {
        final BeanDefinitionHolder containerHolder = createKafkaContainerDefinitionHolder(topicName);
        
        final BeanDefinitionHolder messageHandlerHolder = createMessageHandlerDefinitionHolder(consumerDetails);
        
        final BeanDefinitionHolder channelHolder = createChannelDefinitionHolder(messageHandlerHolder.getBeanName());
        
        final BeanDefinitionHolder adapterHolder = createKafkaAdapterDefinitionHolder(
                containerHolder.getBeanName(), channelHolder.getBeanName(), consumerDetails);
        
        return new HashSet<BeanDefinitionHolder>(Arrays.asList(containerHolder, messageHandlerHolder, channelHolder, adapterHolder));
    }

    private BeanDefinitionHolder createKafkaContainerDefinitionHolder(String topicName) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(KafkaMessageListenerContainer.class);
        
        builder.addConstructorArgReference(KafkaConsumersConfiguration.KAFKA_CONNECTION_FACTORY_BEAN_NAME);
        builder.addConstructorArgValue(topicName);
        
        builder.addPropertyValue("maxFetch", 300 * 1024);
        builder.addPropertyValue("concurrency", 1);
        
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(
                builder.getBeanDefinition(), getContainerBeanName(topicName));
        
        return holder;
    }
    
    private BeanDefinitionHolder createMessageHandlerDefinitionHolder(ConsumerDetails consumerDetails) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ReflectiveDelegatingMessageHandler.class);
        
        builder.addConstructorArgReference(consumerDetails.getConsumerBeanName());
        builder.addConstructorArgValue(consumerDetails.getConsumerMethodName());
        builder.addConstructorArgValue(consumerDetails.getConsumerMethodArgType());
        
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(
                builder.getBeanDefinition(), getMessageHandlerBeanName(consumerDetails));
        
        return holder;
    }
    
    private BeanDefinitionHolder createChannelDefinitionHolder(String messageHandlerBeanName) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ChannelFactory.class);
        
        builder.addPropertyReference("messageHandler", messageHandlerBeanName);
        
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(
                builder.getBeanDefinition(), getChannelBeanName(messageHandlerBeanName));
        
        return holder;      
    }
    
    private BeanDefinitionHolder createKafkaAdapterDefinitionHolder(
            String containerBeanName, String channelBeanName, ConsumerDetails consumerDetails) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(KafkaMessageDrivenChannelAdapter.class);
        
        builder.addConstructorArgReference(containerBeanName);

        builder.addPropertyValue("keyDecoder", instantiateDecoderClass(consumerDetails.getKeyDecoderClass(), consumerDetails.getConsumerMethodArgType()));
        builder.addPropertyValue("payloadDecoder", instantiateDecoderClass(consumerDetails.getPayloadDecoderClass(), consumerDetails.getConsumerMethodArgType()));
        builder.addPropertyReference("outputChannel", channelBeanName);
        
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(
                builder.getBeanDefinition(), getAdapterBeanName(channelBeanName));
        
        return holder;
    }
    
    private String firstLetterToUpper(String stringToModify) {
        return stringToModify.substring(0, 1).toUpperCase() + stringToModify.substring(1);
    }
    
    private <T> T instantiateDecoderClass(Class<T> decoderClass, Class<?> argType) {
        try {
            final T decoder = decoderClass.getConstructor().newInstance();
            
            if (decoder instanceof TypeSettableDecoder) {
                ((TypeSettableDecoder) decoder).setType(argType);
            }
            
            return decoder;
        } catch (Exception e) {
            throw new GregorException("Unable to instantiate decoder class", e);
        } 
    }
    
    //TODO Sort out these names...too long!
    
    private String getContainerBeanName(String topicName) {
        return BEAN_NAME_PREFIX + "Container" + firstLetterToUpper(topicName) + containerCount.incrementAndGet();
    }
    
    private String getChannelBeanName(String messageHandlerBeanName) {
        return BEAN_NAME_PREFIX + "ChannelFor" + messageHandlerBeanName;
    }
    
    private String getMessageHandlerBeanName(ConsumerDetails consumerDetails) {
        return BEAN_NAME_PREFIX + "HandlerDelegatingTo" 
                + consumerDetails.getConsumerBeanName() + ":" + consumerDetails.getConsumerMethodName();
    }
    
    private String getAdapterBeanName(String channelName) {
        return BEAN_NAME_PREFIX + "AdapterFor" + channelName;
    }
}
