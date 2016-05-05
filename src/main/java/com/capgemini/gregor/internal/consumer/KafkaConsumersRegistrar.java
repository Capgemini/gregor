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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.integration.kafka.serializer.common.StringDecoder;
import org.springframework.util.ClassUtils;

import com.capgemini.gregor.GregorException;
import com.capgemini.gregor.KafkaConsumer;
import com.capgemini.gregor.internal.KafkaBeanDefinitionFactory;

/**
 * Scans for any bean methods that have been annotated with the KafkaConsumer
 * annotation and registers the beans required for them to function.
 * 
 * @author craigwilliams84
 *
 */
public class KafkaConsumersRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {
    
    private ClassLoader beanClassloader;
    
    @Override
    public void setBeanClassLoader(ClassLoader classloader) {
        beanClassloader = classloader;    
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        for(BeanDefinitionHolder consumerBean : getConsumerBeans(registry))
        {
            registerBeanDefinitionsForConsumerBean(consumerBean, registry);
        }
    }
    
    private Set<BeanDefinitionHolder> getConsumerBeans(BeanDefinitionRegistry registry) {
        
        Set<BeanDefinitionHolder> candidates = new HashSet<BeanDefinitionHolder>();
        for (String beanName : registry.getBeanDefinitionNames()) {
            final BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                final AnnotatedBeanDefinition potentialQuintComponent = (AnnotatedBeanDefinition) beanDefinition;
                
                final Class<?> beanClass = getClassForBean((AnnotatedBeanDefinition) potentialQuintComponent);
                
                if (isConsumerAnnotatedClass(beanClass)) {
                    
                    candidates.add(new BeanDefinitionHolder(beanDefinition, beanName));
                } 
            }
        }
        
        return candidates;
    }
    
    private void registerBeanDefinitionsForConsumerBean(BeanDefinitionHolder consumerBeanHolder, BeanDefinitionRegistry registry) {
        final AnnotatedBeanDefinition consumerBean = (AnnotatedBeanDefinition) consumerBeanHolder.getBeanDefinition();
        
        final Set<BeanDefinitionHolder> definitionHolders = getBeanDefinitionsDerivedFromQuintAnnotatedBean(registry, consumerBean, consumerBeanHolder.getBeanName());
        
        for(BeanDefinitionHolder holder : definitionHolders) {
            registry.registerBeanDefinition(holder.getBeanName(), holder.getBeanDefinition());
        }
    }
    
    private boolean isConsumerAnnotatedClass(Class<?> clazz) {
        return doesClassHaveMethodAnnotation(clazz, KafkaConsumer.class);
    }
    
    private Set<BeanDefinitionHolder> getBeanDefinitionsDerivedFromQuintAnnotatedBean(BeanDefinitionRegistry registry, 
            AnnotatedBeanDefinition quintBeanDefinition, String beanName) {
        final Set<BeanDefinitionHolder> beanHolders = new HashSet<BeanDefinitionHolder>();
        
        final Class<?> beanClass = getClassForBean(quintBeanDefinition);
        
        for (Method method : beanClass.getMethods()) {
            if (method.isAnnotationPresent(getConsumerAnnotationClass())) {
                final KafkaConsumer consumer = method.getAnnotation(KafkaConsumer.class);
                
                final Set<BeanDefinitionHolder> holders =  getDefinitionFactory().create(
                        createConsumerDetails(consumer, beanName, method));
                
                beanHolders.addAll(holders);
            }
        }
        
        return beanHolders;
    }
    
    protected KafkaBeanDefinitionFactory<ConsumerDetails> getDefinitionFactory() {
        return KafkaConsumerBeanDefinitionFactory.getInstance();
    }
    
    protected Class<? extends Annotation> getConsumerAnnotationClass() {
        return KafkaConsumer.class;
    }
    
    private Class<?> getClassForBean(AnnotatedBeanDefinition beanDefinition) {
        try {
            return ClassUtils.forName(beanDefinition.getMetadata().getClassName(), beanClassloader);
        } catch (ClassNotFoundException e) {
            throw new GregorException("Can't find the bean class...this should not happen!", e);
        }
    }
    
    private boolean doesClassHaveMethodAnnotation(Class<?> clazz, Class<? extends Annotation> annotationType) {
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(annotationType)) {
                return true;
            }
        }
        
        return false;
    }
    
    private ConsumerDetails createConsumerDetails(KafkaConsumer consumer, String consumerBeanName, Method consumerMethod) {
        
        //TODO method validation
        final Class<?> payloadDecoderClass = getPayloadDecoder(consumer);
        final ConsumerDetails details = new ImmutableConsumerDetails(consumer.topic(), consumerBeanName, 
                consumerMethod.getName(), consumerMethod.getParameterTypes()[0], payloadDecoderClass, consumer.keyDecoder());
        
        return details;
    }
    
    private Class<?> getPayloadDecoder(KafkaConsumer consumer) {
        if (consumer.payloadDecoder() != void.class) {
            return consumer.payloadDecoder();
        } else {
            switch (consumer.payloadContent()) {
                case JSON: 
                    return JSONDecoder.class;
                case STRING:
                    return StringDecoder.class;
                default:
                    return JSONDecoder.class;
            }
        }
    }
}
