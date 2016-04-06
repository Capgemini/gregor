package com.capgemini.gregor.internal.producer;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import com.capgemini.gregor.EnableKafkaProducers;
import com.capgemini.gregor.GregorParseException;
import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaProducer;
import com.capgemini.gregor.TestObject;
import com.capgemini.gregor.internal.DetailsParser;
import com.capgemini.gregor.internal.KafkaBeanDefinitionFactory;
import com.capgemini.gregor.internal.producer.client.SimpleKafkaClient;

public class KafkaProducersRegistrarTest {

    private static final String BEAN_NAME_TO_REGISTER_1 = "testBean1";
    
    private static final String BEAN_NAME_TO_REGISTER_2 = "testBean2";

    private static final String TEST_TOPIC = "testTopic";
    
    private List<ProducerDetails> producerDetailsPassedToFactory;
    
    private List<MethodMetadata> methodMetadataPassedToParser;
    
    private ProducerDetails producerDetails;
    
    @Before
    public void init() {
        producerDetailsPassedToFactory = null;
        methodMetadataPassedToParser = new ArrayList<MethodMetadata>();
        producerDetails = Mockito.mock(ProducerDetails.class);
    }
    
    @Test
    public void testBeansRegisteredCorrectly() {
        
        final BeanDefinitionRegistry registry = wireUpAndCallRegisterBeanDefinitions();
        
        assertEquals("Bean 1 not registered correctly", TestObject.class.getName(), registry.getBeanDefinition(BEAN_NAME_TO_REGISTER_1).getBeanClassName());
        
        assertEquals("Bean 2 not registered correctly", Object.class.getName(), registry.getBeanDefinition(BEAN_NAME_TO_REGISTER_2).getBeanClassName());
    }
    
    @Test
    public void testCorrectProducerDetailsPassedToFactory() {
        
        wireUpAndCallRegisterBeanDefinitions();
        
        assertEquals("Incorrect number of producer details", 1, producerDetailsPassedToFactory.size());        
        assertEquals("Incorrect producer details passed to factory", producerDetails, producerDetailsPassedToFactory.get(0));
    }
    
    @Test
    public void testCorrectMethodMetadataPassedToParser() {
        
        wireUpAndCallRegisterBeanDefinitions();
        
        assertEquals("Incorrect number of method meta", 1, methodMetadataPassedToParser.size());        
    
        final MethodMetadata metadata = methodMetadataPassedToParser.get(0);
        final Map<String, Object> attributes = metadata.getAnnotationAttributes(KafkaProducer.class.getCanonicalName());
        assertEquals("Incorrect topic set in metadata", TEST_TOPIC, attributes.get("topic"));
        assertEquals("Incorrect client class in metadata", SimpleKafkaClient.class.getName(), metadata.getDeclaringClassName());
        assertEquals("Incorrect method name metadata", "produce", metadata.getMethodName());
    }
    
    private BeanDefinitionRegistry wireUpAndCallRegisterBeanDefinitions() {
        final BeanDefinitionHolder holder1 = createBeanDefinitionHolder(TestObject.class, BEAN_NAME_TO_REGISTER_1);
        final BeanDefinitionHolder holder2 = createBeanDefinitionHolder(Object.class, BEAN_NAME_TO_REGISTER_2);
        final KafkaProducersRegistrar underTest = new KafkaProducersRegistrarForTest(holder1, holder2);
        underTest.setResourceLoader(new DefaultResourceLoader());
        
        
        final BeanDefinitionRegistry registry = createBeanDefinitionRegistry();
        
        underTest.registerBeanDefinitions(mockAnnotationMetadata(), registry);
        
        return registry;
    }
    
    private BeanDefinitionRegistry createBeanDefinitionRegistry() {
        final BeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
                
        return registry;
    }
    
    private BeanDefinitionHolder createBeanDefinitionHolder(Class<?> beanClass, String beanName) {
        return new BeanDefinitionHolder(createBeanDefinition(beanClass), beanName);
    }
    
    private BeanDefinition createBeanDefinition(Class<?> beanClass) {
        return new AnnotatedGenericBeanDefinition(beanClass);
    }
    
    private AnnotationMetadata mockAnnotationMetadata() {
        final AnnotationMetadata mockMetadata = Mockito.mock(AnnotationMetadata.class);
        
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("value", new String[] {SimpleKafkaClient.class.getPackage().getName()});
        attributes.put("basePackages", new String[]{});
        attributes.put("basePackageClasses", new Class[]{});
        
        Mockito.when(mockMetadata.getAnnotationAttributes(
                EnableKafkaProducers.class.getCanonicalName())).thenReturn(attributes);
        
        return mockMetadata;
    }
    
    private class KafkaProducersRegistrarForTest extends KafkaProducersRegistrar {
        
        private KafkaBeanDefinitionFactory<List<ProducerDetails>> factory;
        
        private KafkaProducersRegistrarForTest(BeanDefinitionHolder... holdersReturnedByFactory) {
            this.factory = new DummyKafkaProducerBeanDefinitionFactory(holdersReturnedByFactory);
        }
        
        @Override
        protected KafkaBeanDefinitionFactory<List<ProducerDetails>> getDefinitionFactory() {
            return factory;
        }
        
        protected DetailsParser<ProducerDetails> getDetailsParser() {
            return new DummyProducerDetailsParser();
        }
    }
    
    private class DummyKafkaProducerBeanDefinitionFactory implements KafkaBeanDefinitionFactory<List<ProducerDetails>> {
        
        BeanDefinitionHolder[] holders;
        
        private DummyKafkaProducerBeanDefinitionFactory(BeanDefinitionHolder... holders) {
            this.holders = holders;
        }
        
        @Override
        public Set<BeanDefinitionHolder> create(List<ProducerDetails> producerDetails) {
            producerDetailsPassedToFactory = producerDetails;
            
            return new HashSet<BeanDefinitionHolder>(Arrays.asList(holders));
        }  
    }
    
    private class DummyProducerDetailsParser implements DetailsParser<ProducerDetails> {

        @Override
        public ProducerDetails parse(MethodMetadata methodMetadata)
                throws GregorParseException {

            methodMetadataPassedToParser.add(methodMetadata);
            return producerDetails;
        }
        
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @KafkaClient
    public @interface AnnotationKafkaClient {
    }
}
