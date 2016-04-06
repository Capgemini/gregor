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

/**
 * Immutable consumer details.
 * 
 * @author craigwilliams84
 *
 */
public class ImmutableConsumerDetails implements ConsumerDetails {
    
    private String topicName;
    
    private String consumerBeanName;
    
    private String consumerMethodName;
    
    private Class<?> consumerMethodArgType;
    
    private Class<?> payloadDecoderClass;
    
    private Class<?> keyDecoderClass;

    public ImmutableConsumerDetails(String topicName, String consumerBeanName, String consumerMethodName, 
            Class<?> consumerMethodArgType, Class<?> payloadDecoderClass, Class<?> keyDecoderClass) {
        this.topicName = topicName;
        this.consumerBeanName = consumerBeanName;
        this.consumerMethodName = consumerMethodName;
        this.consumerMethodArgType = consumerMethodArgType;
        this.payloadDecoderClass = payloadDecoderClass;
        this.keyDecoderClass = keyDecoderClass;
    }

    public String getTopicName() {
        return topicName;
    }
    
    public String getConsumerBeanName() {
        return consumerBeanName;
    }

    public String getConsumerMethodName() {
        return consumerMethodName;
    }

    public Class<?> getConsumerMethodArgType() {
        return consumerMethodArgType;
    }
    
    public Class<?> getPayloadDecoderClass() {
        return payloadDecoderClass;
    }
    
    public Class<?> getKeyDecoderClass() {
        return keyDecoderClass;
    }
}
