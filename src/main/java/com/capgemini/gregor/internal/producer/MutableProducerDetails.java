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

import java.lang.reflect.Method;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class MutableProducerDetails implements ProducerDetails {

    private String topicName;
    
    private Method producerMethod;
    
    private Class<?> clientClass;
    
    private Class<?> keyClassType;
    
    private Class<?> payloadClassType;
    
    private Class<? extends Serializer<?>> keySerializerClass = StringSerializer.class;
    
    private Class<? extends Serializer<?>> payloadSerializerClass = StringSerializer.class;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Method getProducerMethod() {
        return producerMethod;
    }

    public void setProducerMethod(Method producerMethod) {
        this.producerMethod = producerMethod;
    }

    public Class<?> getClientClass() {
        return clientClass;
    }

    public void setClientClass(Class<?> clientClass) {
        this.clientClass = clientClass;
    }

    public Class<?> getKeyClassType() {
        return keyClassType;
    }

    public void setKeyClassType(Class<?> keyClassType) {
        this.keyClassType = keyClassType;
    }

    public Class<?> getPayloadClassType() {
        return payloadClassType;
    }

    public void setPayloadClassType(Class<?> payloadClassType) {
        this.payloadClassType = payloadClassType;
    }

    public Class<? extends Serializer<?>> getKeySerializerClass() {
        return keySerializerClass;
    }

    public void setKeySerializerClass(
            Class<? extends Serializer<?>> keySerializerClass) {
        this.keySerializerClass = keySerializerClass;
    }

    public Class<? extends Serializer<?>> getPayloadSerializerClass() {
        return payloadSerializerClass;
    }

    public void setPayloadSerializerClass(
            Class<? extends Serializer<?>> payloadSerializerClass) {
        this.payloadSerializerClass = payloadSerializerClass;
    }

}
