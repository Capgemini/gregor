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

import org.apache.kafka.clients.producer.Producer;
import org.springframework.integration.kafka.support.ProducerFactoryBean;
import org.springframework.integration.kafka.support.ProducerMetadata;

import com.capgemini.gregor.GregorException;
import com.capgemini.gregor.internal.KafkaSettings;

/**
 * A Kafka producer factory that delegates creation to the Spring FactoryBean.
 * 
 * @author craigwilliams84
 *
 * @param <K> Producer Key
 * @param <V> Producer Value
 */
public class BeanFactoryDelegatingProducerFactory<K,V> implements ProducerFactory<K, V> {

    @Override
    public Producer<K,V> create(ProducerMetadata<K,V> metadata, KafkaSettings settings) {
        final ProducerFactoryBean<K,V> factoryBeanDelegate = getDelegate(metadata, settings);
        
        try {
            return factoryBeanDelegate.getObject();
        } catch (Exception e) {
            throw new GregorException("Unable to create producer", e);
        }
    }
    
    protected ProducerFactoryBean<K,V> getDelegate(ProducerMetadata<K,V> metadata, KafkaSettings settings) {
        return new ProducerFactoryBean<K,V>(metadata, settings.getBrokerAddress());
    }
}
