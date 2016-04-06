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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.capgemini.gregor.EnableKafkaProducers;
import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaProducer;
import com.capgemini.gregor.Key;
import com.capgemini.gregor.Payload;
import com.capgemini.gregor.internal.DetailsParser;
import com.capgemini.gregor.internal.KafkaBeanDefinitionFactory;

public class KafkaProducersRegistrar implements ImportBeanDefinitionRegistrar,
    ResourceLoaderAware, BeanClassLoaderAware {
 // patterned after Spring Integration IntegrationComponentScanRegistrar

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    public KafkaProducersRegistrar() {
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {

        Set<String> basePackages = getBasePackages(importingClassMetadata);

        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.addIncludeFilter(new AnnotationTypeFilter(getClientAnnotation()));
        scanner.setResourceLoader(this.resourceLoader);

        final DetailsParser<ProducerDetails> detailsParser = getDetailsParser();
        
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner
                    .findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    // verify annotated class is an interface
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@KafkaClient can only be specified on an interface");
                    
                    final List<ProducerDetails> producerDetailsForClass = new ArrayList<ProducerDetails>();
                    for (MethodMetadata methodMetadata : annotationMetadata.getAnnotatedMethods(getMethodAnnotation().getCanonicalName())) {
                        producerDetailsForClass.add(detailsParser.parse(methodMetadata));                       
                    }
                    
                    Set<BeanDefinitionHolder> holders = createBeanDefinitions(producerDetailsForClass);
                    
                    for (BeanDefinitionHolder holder : holders) {
                        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
                    }
                }
            }
        }
    }

    private Set<BeanDefinitionHolder> createBeanDefinitions(List<ProducerDetails> details) {
        return getDefinitionFactory().create(details);
    }
    
    protected DetailsParser<ProducerDetails> getDetailsParser() {
        return new KafkaProducerDetailsParser(getMethodAnnotation(), getPayloadAnnotation(), getKeyAnnotation());
    }
    
    protected KafkaBeanDefinitionFactory<List<ProducerDetails>> getDefinitionFactory() {
        return KafkaProducerBeanDefinitionFactory.getInstance();
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {

            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    // TODO until SPR-11711 will be resolved
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata().getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(
                                    beanDefinition.getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(beanDefinition
                                    .getMetadata().getClassName(),
                                    KafkaProducersRegistrar.this.classLoader);
                            return (!target.isAnnotation() 
                                    && beanDefinition.getMetadata().hasAnnotatedMethods(getMethodAnnotation().getCanonicalName()));
                        }
                        catch (Exception ex) {
                            this.logger.error("Could not load target class: "
                                    + beanDefinition.getMetadata().getClassName(), ex);

                        }
                    }
                    return true;
                }
                return false;

            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(getEnableAnnotation().getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata
                    .getClassName()));
        }
        return basePackages;
    }
    
    protected Class<? extends Annotation> getEnableAnnotation() {
        return EnableKafkaProducers.class;
    }
    
    protected Class<? extends Annotation> getClientAnnotation() {
        return KafkaClient.class;
    }
    
    protected Class<? extends Annotation> getMethodAnnotation() {
        return KafkaProducer.class;
    }
    
    protected Class<? extends Annotation> getPayloadAnnotation() {
        return Payload.class;
    }
    
    protected Class<? extends Annotation> getKeyAnnotation() {
        return Key.class;
    }
}
