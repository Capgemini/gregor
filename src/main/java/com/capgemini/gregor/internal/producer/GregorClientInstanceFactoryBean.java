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

import java.util.List;

import org.springframework.beans.factory.FactoryBean;

import com.capgemini.gregor.internal.KafkaSettings;

/**
 * Factory bean for creating a bean implementation of a Gregor annotated interface.
 * 
 * @author craigwilliams84
 *
 */
public class GregorClientInstanceFactoryBean implements FactoryBean<Object>{

    private Class<?> type;
    
    private List<ProducerDetails> producerDetails;
    
    private ProducerFactory<Object,Object> producerFactory;
    
    private KafkaSettings kafkaSettings;
        
    public GregorClientInstanceFactoryBean(List<ProducerDetails> producerDetails, ProducerFactory<Object,Object> producerFactory) {
        this.producerDetails = producerDetails;
        this.producerFactory = producerFactory;
        
        type = producerDetails.get(0).getClientClass();
    }
    
    @Override
    public Object getObject() throws Exception {
        return getClientInstanceFactory().build(type, producerDetails);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setKafkaSettings(KafkaSettings kafkaSettings) {
        this.kafkaSettings = kafkaSettings;
    }

    protected GregorClientInstanceFactory getClientInstanceFactory() {
        return new ProxyGregorClientInstanceFactory(producerFactory, kafkaSettings);
    }
}
