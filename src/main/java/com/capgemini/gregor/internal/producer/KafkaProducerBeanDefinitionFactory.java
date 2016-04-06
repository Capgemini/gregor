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

package com.capgemini.gregor.internal.producer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import com.capgemini.gregor.internal.KafkaBeanDefinitionFactory;
import com.capgemini.gregor.internal.KafkaCommonConfiguration;

/**
 * BeanDefinitionFactory for creating bean definitions for a single annotated gregor client interface (but potentially multiple producer methods).
 * 
 * @author craigwilliams84
 *
 */
public class KafkaProducerBeanDefinitionFactory implements KafkaBeanDefinitionFactory<List<ProducerDetails>>{

    private static KafkaProducerBeanDefinitionFactory INSTANCE;
    
    private AtomicInteger producerCount = new AtomicInteger(0);
    
    private KafkaProducerBeanDefinitionFactory() {
        //Private singleton constructor
    }
    
    public static KafkaProducerBeanDefinitionFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new KafkaProducerBeanDefinitionFactory();
        }
        
        return INSTANCE;
    }
    
    @Override
    public Set<BeanDefinitionHolder> create(List<ProducerDetails> details) {
        
        final Set<BeanDefinitionHolder> holders = new HashSet<BeanDefinitionHolder>();
        
        holders.add(createGregorClientInstanceFactoryBeanDefinitionHolder(details));
        
        return holders;
    }
    
    private BeanDefinitionHolder createGregorClientInstanceFactoryBeanDefinitionHolder(List<ProducerDetails> producerDetails) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(GregorClientInstanceFactoryBean.class);
        
        builder.addConstructorArgValue(producerDetails);
        builder.addConstructorArgValue(new BeanFactoryDelegatingProducerFactory<Object, Object>());
        
        builder.addPropertyReference("kafkaSettings", KafkaCommonConfiguration.KAFKA_SETTINGS_BEAN_NAME);
        
        final BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), getGregorClientFactoryBeanName());
        
        return holder;
    }
    
    private String getGregorClientFactoryBeanName() {
        return "gregorClient" + producerCount.incrementAndGet();
    }
}
