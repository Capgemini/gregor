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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.integration.kafka.support.ProducerMetadata;

import com.capgemini.gregor.GregorException;
import com.capgemini.gregor.internal.KafkaSettings;

/**
 * A GregorClientInstanceFactory implemented via a java proxy.
 * 
 * @author craigwilliams84
 *
 */
public class ProxyGregorClientInstanceFactory implements GregorClientInstanceFactory {
    
    private ProducerFactory<Object,Object> producerFactory;
    
    private KafkaSettings settings;
    
    public ProxyGregorClientInstanceFactory(ProducerFactory<Object,Object> producerFactory, KafkaSettings settings) {
        this.producerFactory = producerFactory;
        this.settings = settings;
    }
    
    @SuppressWarnings("unchecked")
    public<T> T build(Class<T> type, List<ProducerDetails> producerDetailsList) {
        
        final DelegatingInvocationHandler delegatingHandler = createDelegatingInvocationHandler();
        
        for (ProducerDetails producerDetails : producerDetailsList) {
            delegatingHandler.addDelegateHandler(producerDetails.getProducerMethod(), createProducerInvocationHandler(producerDetails));
        }       
        
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, delegatingHandler);
    }
    
    protected InvocationHandler createProducerInvocationHandler(ProducerDetails producerDetails) {
        
        final Producer<Object,Object> producer = producerFactory.create(createProducerMetadata(producerDetails), settings);
        
        return new KafkaProducerInvocationHandler(producer, producerDetails);
    }
    
    protected DelegatingInvocationHandler createDelegatingInvocationHandler() {
        return new DefaultDelegatingInvocationHandler();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ProducerMetadata<Object,Object> createProducerMetadata(ProducerDetails details) {
        return new ProducerMetadata(details.getTopicName(), details.getKeyClassType(), details.getPayloadClassType(), 
                instantiateSerializerClass(details.getKeySerializerClass()), instantiateSerializerClass(details.getPayloadSerializerClass()));
    }
    
    private <T extends Serializer<?>> T instantiateSerializerClass(Class<T> serializerClass) {
        try {
            final T serializer = serializerClass.getConstructor().newInstance();
            
            return serializer;
        } catch (Exception e) {
            throw new GregorException("Unable to instantiate serializer class", e);
        } 
    }
}
