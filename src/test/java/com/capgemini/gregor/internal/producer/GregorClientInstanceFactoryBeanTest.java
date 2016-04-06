package com.capgemini.gregor.internal.producer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.capgemini.gregor.internal.producer.client.SimpleKafkaClient;

public class GregorClientInstanceFactoryBeanTest {
    
    @Test
    public void testGetObject() throws Exception {
        final List<ProducerDetails> detailsList = createProducerDetailsList();
        
        final ProxyGregorClientInstanceFactory mockInstanceFactory = Mockito.mock(ProxyGregorClientInstanceFactory.class);
        
        final GregorClientInstanceFactoryBean underTest = createObjectUnderTest(detailsList, mockProducerFactory(), mockInstanceFactory);
        
        final SimpleKafkaClient mockClient = Mockito.mock(SimpleKafkaClient.class);
        
        Mockito.when(mockInstanceFactory.build(SimpleKafkaClient.class, detailsList)).thenReturn(mockClient);
        
        assertEquals("Incorrect return value from getObject", mockClient, underTest.getObject());
    }
    
    @Test
    public void testGetObjectType() {
        assertEquals("Incorrect type returned from getObjectType", SimpleKafkaClient.class, createObjectUnderTest().getObjectType());
    }
    
    @Test
    public void testIsSingleton() {
        assertEquals("IsSingleton returned incorrect value", true, createObjectUnderTest().isSingleton());
    }
    
    private GregorClientInstanceFactoryBean createObjectUnderTest() {
        return createObjectUnderTest(createProducerDetailsList(), mockProducerFactory(), null);
    }
    
    private GregorClientInstanceFactoryBean createObjectUnderTest(List<ProducerDetails> detailsList, ProducerFactory<Object, Object> producerFactory, ProxyGregorClientInstanceFactory instanceFactory) {
        return new GregorClientInstanceFactoryBeanForTest(detailsList, producerFactory, instanceFactory);
    }
    
    private List<ProducerDetails> createProducerDetailsList() {
        List<ProducerDetails> detailsList = new ArrayList<ProducerDetails>();
        
        final MutableProducerDetails details = new MutableProducerDetails();
        details.setClientClass(SimpleKafkaClient.class);
        
        detailsList.add(details);
        
        return detailsList;
    }
    
    @SuppressWarnings("unchecked")
    private ProducerFactory<Object, Object> mockProducerFactory() {
        return Mockito.mock(ProducerFactory.class);
    }
    
    private class GregorClientInstanceFactoryBeanForTest extends GregorClientInstanceFactoryBean {

        private ProxyGregorClientInstanceFactory instanceFactory;
        
        public GregorClientInstanceFactoryBeanForTest(List<ProducerDetails> producerDetails,
                ProducerFactory<Object, Object> producerFactory, ProxyGregorClientInstanceFactory instanceFactory) {
            super(producerDetails, producerFactory);
            this.instanceFactory = instanceFactory;
        }
        
        @Override
        protected ProxyGregorClientInstanceFactory getClientInstanceFactory() {
            return instanceFactory;
        }
    }
}
