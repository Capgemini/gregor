package com.capgemini.gregor.internal.producer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.integration.kafka.support.ProducerMetadata;

import com.capgemini.gregor.internal.KafkaSettings;
import com.capgemini.gregor.internal.producer.client.SimpleKafkaClient;

public class ProxyGregorClientInstanceFactoryTest {
    
    private Object valuePassedToProducer;
    
    @Before
    public void reset() {
        valuePassedToProducer = null;
    }
    
    @Test
    public void testClientBuilds() throws NoSuchMethodException, SecurityException {       
        
        final SimpleKafkaClient client = executeBuild();
        
        assertNotNull("Client is null", client);
    }
    
    @Test
    public void testClientFunctions() throws NoSuchMethodException, SecurityException {       
        
        final SimpleKafkaClient client = executeBuild();
        
        assertNotNull("Client is null", client);
        
        client.produce("Testing 123");
        assertEquals("Incorrect value passed to producer", "Testing 123", valuePassedToProducer);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SimpleKafkaClient executeBuild() throws NoSuchMethodException, SecurityException {
        final ProducerFactory<Object, Object> mockProducerFactory = mockProducerFactory();
        final ArgumentCaptor<ProducerMetadata> metadataCaptor = ArgumentCaptor.forClass(ProducerMetadata.class);
        
        final KafkaSettings mockSettings = mockKafkaSettings();
        
        final Producer mockProducer = mockProducer();
        Mockito.when(mockProducerFactory.create(metadataCaptor.capture(), eq(mockSettings))).thenReturn(mockProducer);
        
        final ProxyGregorClientInstanceFactory underTest = new ProxyGregorClientInstanceFactory(mockProducerFactory, mockSettings);
        
        return underTest.build(SimpleKafkaClient.class, createProducerDetails());      
    }
    
    private List<ProducerDetails> createProducerDetails() throws NoSuchMethodException, SecurityException {
        final MutableProducerDetails details = new MutableProducerDetails();
        details.setClientClass(SimpleKafkaClient.class);
        details.setKeyClassType(Void.class);
        details.setPayloadClassType(String.class);
        details.setKeySerializerClass(StringSerializer.class);
        details.setPayloadSerializerClass(StringSerializer.class);
        details.setProducerMethod(SimpleKafkaClient.class.getDeclaredMethod("produce", String.class));
        details.setTopicName("testTopic");
        
        return Arrays.asList((ProducerDetails)details);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Producer<Object, Object> mockProducer() {
        final Producer<Object, Object> mockProducer = Mockito.mock(Producer.class);
        
        Mockito.when(mockProducer.send(isA(ProducerRecord.class))).thenAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final ProducerRecord record = invocation.getArgumentAt(0, ProducerRecord.class);
                
                valuePassedToProducer = record.value();
                
                return null;
            }
            
        });
        
        return mockProducer;
    }
    
    @SuppressWarnings("unchecked")
    private ProducerFactory<Object, Object> mockProducerFactory() {
        final ProducerFactory<Object, Object> mockFactory = Mockito.mock(ProducerFactory.class);
    
        return mockFactory;
    }
    
    private KafkaSettings mockKafkaSettings() {
        final KafkaSettings mockSettings = Mockito.mock(KafkaSettings.class);
    
        return mockSettings;
    }
}
