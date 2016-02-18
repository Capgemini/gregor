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

import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.integration.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.integration.kafka.serializer.common.StringDecoder;

import com.capgemini.gregor.TestObject;

public class DefaultKafkaConsumerBeanDefinitionFactoryTest {

    private static final String TEST_TOPIC_NAME = "testTopic";
    
    private DefaultKafkaConsumerBeanDefinitionFactory underTest = DefaultKafkaConsumerBeanDefinitionFactory.getInstance();
    
    @Test
    public void testKafkaContainerBeanDefinitionAdded() {
        runBeanDefinitionExistanceTest(KafkaMessageListenerContainer.class);
    }
    
    @Test
    public void testMessageHandlerBeanDefinitionAdded() {
        runBeanDefinitionExistanceTest(ReflectiveDelegatingMessageHandler.class);
    }
    
    @Test
    public void testChannelFactoryBeanDefinitionAdded() {
        runBeanDefinitionExistanceTest(ChannelFactory.class);
    }
    
    @Test
    public void testKafkaAdapterBeanDefinitionAdded() {
        runBeanDefinitionExistanceTest(KafkaMessageDrivenChannelAdapter.class);
    }
    
    private void runBeanDefinitionExistanceTest(Class<?> beanClassToCheck) {
        final Set<BeanDefinitionHolder> holders = underTest.create(TEST_TOPIC_NAME, createConsumerDetails());
        
        assertBeanDefinitionExistsOfType(holders, KafkaMessageListenerContainer.class);
    }
    
    private void assertBeanDefinitionExistsOfType(Set<BeanDefinitionHolder> holders, Class<?> typeClass) {
        for (BeanDefinitionHolder holder : holders) {
            final BeanDefinition definition = holder.getBeanDefinition();
            
            if (definition.getBeanClassName().equals(typeClass.getName())) {
                return;
            }
        }
        
        //Not found if we get this far
        TestCase.fail("Bean definition for class: " + typeClass.getName() + " not created");
    }
    
    private ConsumerDetails createConsumerDetails() {
        return new ImmutableConsumerDetails("consumerBean", 
                "consumerMethod", TestObject.class, StringDecoder.class, StringDecoder.class);
    }
}
