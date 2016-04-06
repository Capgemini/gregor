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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.core.type.MethodMetadata;

import com.capgemini.gregor.GregorParseException;
import com.capgemini.gregor.internal.DetailsParser;

public class KafkaProducerDetailsParser implements DetailsParser<ProducerDetails> {

    private static final Class<?> DEFAULT_KEY_CLASS_TYPE = Void.class;
    
    private static final Class<?> DEFAULT_KEY_SERIALIZER_CLASS = StringSerializer.class;
    
    private static final Class<?> DEFAULT_PAYLOAD_SERIALIZER_CLASS = StringSerializer.class;
    
    private Class<? extends Annotation> methodLevelAnnotation;
    
    private Class<? extends Annotation> payloadAnnotation;
    
    private Class<? extends Annotation> keyAnnotation;
    
    KafkaProducerDetailsParser(Class<? extends Annotation> methodLevelAnnotation, 
            Class<? extends Annotation> payloadAnnotation, Class<? extends Annotation> keyAnnotation) {
        this.methodLevelAnnotation = methodLevelAnnotation;
        this.payloadAnnotation = payloadAnnotation;
        this.keyAnnotation = keyAnnotation;
    }
    
    @Override
    public ProducerDetails parse(MethodMetadata methodMetadata)
            throws GregorParseException {
        final Map<String, Object> attributes = methodMetadata.getAnnotationAttributes(methodLevelAnnotation.getCanonicalName());
        final MutableProducerDetails details = new MutableProducerDetails();
        try {
            details.setTopicName((String)attributes.get("topic"));
            
            final Class<?> declaringClass = Class.forName(methodMetadata.getDeclaringClassName());
            details.setClientClass(declaringClass);
            
            final Method method = getMethodWithName(methodMetadata.getMethodName(), declaringClass);
            details.setProducerMethod(method);
            
            details.setPayloadClassType(establishPayloadType(method));
            
            details.setKeyClassType(establishKeyType(method));
            
            details.setKeySerializerClass(getKeySerializerClass(attributes));
            details.setPayloadSerializerClass(getPayloadSerializerClass(attributes));
            
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return details;
    }
    
    private Class<? extends Serializer<?>> getKeySerializerClass(Map<String, Object> annotationAttributes) {
        return (Class<? extends Serializer<?>>) annotationAttributes.getOrDefault("keySerializer", DEFAULT_KEY_SERIALIZER_CLASS);
    }
    
    private Class<? extends Serializer<?>> getPayloadSerializerClass(Map<String, Object> annotationAttributes) {
        return (Class<? extends Serializer<?>>) annotationAttributes.getOrDefault("payloadSerializer", DEFAULT_PAYLOAD_SERIALIZER_CLASS);
    }
    
    private Class<?> establishKeyType(Method method) {
        
        for (Parameter parameter : method.getParameters()) {
            if (parameter.getAnnotation(getKeyAnnotation()) != null) {
                return parameter.getType();
            }
        }
        
        return DEFAULT_KEY_CLASS_TYPE;
    }
    
    private Class<?> establishPayloadType(Method method) {
        
        final Parameter[] parameters = method.getParameters();
        
        //If there is only 1 parameter, then this must be the payload
        if (parameters.length == 1 ) {
            return parameters[0].getType();
        } else if (parameters.length > 1) {
            //Check if any parameter is annotated with @Payload or @Key
            Integer keyIndex = null;
            
            for (int i = 0; i < parameters.length; i++) {
                final Parameter parameter = parameters[i];
                
                //A payload annotation exists on this parameter...payload found!
                if (parameter.getAnnotation(getPayloadAnnotation()) != null) {
                    return parameter.getType();
                } else if (parameter.getAnnotation(getKeyAnnotation()) != null) {
                    //Key annotation found
                    keyIndex = i;
                }
                
                if (keyIndex != null && parameters.length == 2) {
                    return parameters[Math.abs(keyIndex - 1)].getType();
                } else {
                    //TODO
                }
            }
        }
        
        throw new GregorParseException("Unable to establish the payload type");
    }
    
    protected Class<? extends Annotation> getPayloadAnnotation() {
        return payloadAnnotation;
    }
    
    protected Class<? extends Annotation> getKeyAnnotation() {
        return keyAnnotation;
    }
    
    private Method getMethodWithName(String methodName, Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        
        return null;
    }

}
