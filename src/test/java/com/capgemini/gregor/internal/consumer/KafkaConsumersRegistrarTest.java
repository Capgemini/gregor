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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.integration.kafka.serializer.common.StringDecoder;

import com.capgemini.gregor.KafkaConsumer;
import com.capgemini.gregor.PayloadContent;
import com.capgemini.gregor.TestObject;
import com.capgemini.gregor.internal.KafkaBeanDefinitionFactory;

public class KafkaConsumersRegistrarTest {
    private static final String BEAN_NAME_TO_REGISTER_1 = "testBean1";
    
    private static final String BEAN_NAME_TO_REGISTER_2 = "testBean2";

    private static final String CONSUMER_BEAN_NAME = "consumerBean";

    private static final String TEST_TOPIC = "testTopic";
    
    private ConsumerDetails consumerDetailsPassedToFactory;
    
    @After
    public void reset() {
        consumerDetailsPassedToFactory = null;
    }
    
    @Test
    public void testBeansRegisteredCorrectly() {
        
        final BeanDefinitionRegistry registry = wireUpAndCallRegisterBeanDefinitions();
        
        assertEquals("Bean 1 not registered correctly", TestObject.class.getName(), registry.getBeanDefinition(BEAN_NAME_TO_REGISTER_1).getBeanClassName());
        
        assertEquals("Bean 2 not registered correctly", Object.class.getName(), registry.getBeanDefinition(BEAN_NAME_TO_REGISTER_2).getBeanClassName());
    }
    
    @Test
    public void testCorrectConsumerDetailsPassedToFactory() {
        
        wireUpAndCallRegisterBeanDefinitions();
        
        assertEquals("Incorrect topic name set on consumer details", TEST_TOPIC, consumerDetailsPassedToFactory.getTopicName()); 
        assertEquals("Incorrect bean name set on consumer details", CONSUMER_BEAN_NAME, consumerDetailsPassedToFactory.getConsumerBeanName());
        assertEquals("Incorrect consumer method name set on consumer details", "consume", consumerDetailsPassedToFactory.getConsumerMethodName());  
        assertEquals("Incorrect consumer method arg set on consumer details", TestObject.class, consumerDetailsPassedToFactory.getConsumerMethodArgType());
        assertEquals("Incorrect key decoder set on consumer details", JSONDecoder.class, consumerDetailsPassedToFactory.getKeyDecoderClass());
        assertEquals("Incorrect payload decoder set on consumer details", StringDecoder.class, consumerDetailsPassedToFactory.getPayloadDecoderClass());
    }
    
    private BeanDefinitionRegistry wireUpAndCallRegisterBeanDefinitions() {
        final BeanDefinitionHolder holder1 = createBeanDefinitionHolder(TestObject.class, BEAN_NAME_TO_REGISTER_1);
        final BeanDefinitionHolder holder2 = createBeanDefinitionHolder(Object.class, BEAN_NAME_TO_REGISTER_2);
        final KafkaConsumersRegistrar underTest = new KafkaConsumersRegistrarForTest(holder1, holder2);
        
        final BeanDefinitionRegistry registry = createBeanDefinitionRegistry();
        
        underTest.registerBeanDefinitions(null, registry);
        
        return registry;
    }
    
    private BeanDefinitionRegistry createBeanDefinitionRegistry() {
        final BeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
        
        registry.registerBeanDefinition(CONSUMER_BEAN_NAME, createBeanDefinition(ConsumerAnnotatedClassForTest.class));
        
        return registry;
    }
    
    private BeanDefinitionHolder createBeanDefinitionHolder(Class<?> beanClass, String beanName) {
        return new BeanDefinitionHolder(createBeanDefinition(beanClass), beanName);
    }
    
    private BeanDefinition createBeanDefinition(Class<?> beanClass) {
        return new AnnotatedGenericBeanDefinition(beanClass);
    }
    
    private class KafkaConsumersRegistrarForTest extends KafkaConsumersRegistrar {
       
        private KafkaBeanDefinitionFactory<ConsumerDetails> factory;
        
        private KafkaConsumersRegistrarForTest(BeanDefinitionHolder... holdersReturnedByFactory) {
            this.factory = new DummyKafkaConsumerBeanDefinitionFactory(holdersReturnedByFactory);
        }
        
        @Override
        protected KafkaBeanDefinitionFactory<ConsumerDetails> getDefinitionFactory() {
            return factory;
        }
    }
    
    private class DummyKafkaConsumerBeanDefinitionFactory implements KafkaBeanDefinitionFactory<ConsumerDetails> {
        
        BeanDefinitionHolder[] holders;
        
        private DummyKafkaConsumerBeanDefinitionFactory(BeanDefinitionHolder... holders) {
            this.holders = holders;
        }
        
        @Override
        public Set<BeanDefinitionHolder> create(ConsumerDetails consumerDetails) {
            consumerDetailsPassedToFactory = consumerDetails;
            
            return new HashSet<BeanDefinitionHolder>(Arrays.asList(holders));
        }  
    }
    
    private class ConsumerAnnotatedClassForTest {
        
        @KafkaConsumer(topic = TEST_TOPIC,
                       keyDecoder = JSONDecoder.class,
                       payloadDecoder = StringDecoder.class,
                       payloadContent = PayloadContent.STRING)
        public void consume(TestObject value) {
            
        }
    }
}
