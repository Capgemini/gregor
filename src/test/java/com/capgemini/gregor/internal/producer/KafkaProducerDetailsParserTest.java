package com.capgemini.gregor.internal.producer;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.capgemini.gregor.serializer.JSONSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.type.MethodMetadata;

import com.capgemini.gregor.KafkaClient;
import com.capgemini.gregor.KafkaProducer;
import com.capgemini.gregor.Key;
import com.capgemini.gregor.Payload;
import com.capgemini.gregor.TestObject;
import com.capgemini.gregor.internal.DetailsParser;
import com.capgemini.gregor.internal.producer.client.SimpleKafkaClient;

public class KafkaProducerDetailsParserTest {

    private static final String TEST_TOPIC = "testTopic";
    
    private DetailsParser<ProducerDetails> underTest = 
            new KafkaProducerDetailsParser(KafkaProducer.class, Payload.class, Key.class);
    
    @Test
    public void testParseSimpleClient() throws NoSuchMethodException, SecurityException {
        
        final ProducerDetails details = underTest.parse(mockSimpleClientMethodMetadata());
    
        assertEquals("Incorrect topic name",  TEST_TOPIC, details.getTopicName());
        assertEquals("Incorrect client class",  SimpleKafkaClient.class, details.getClientClass());
        
        final Method expectedMethod = SimpleKafkaClient.class.getMethod("produce",  String.class);
        assertEquals("Incorrect producer method",  expectedMethod, details.getProducerMethod());
        
        assertEquals("Incorrect payload class type",  String.class, details.getPayloadClassType());
        assertEquals("Incorrect key class type",  Void.class, details.getKeyClassType());
        assertEquals("Incorrect payload serializer class", JSONSerializer.class, details.getPayloadSerializerClass());
        assertEquals("Incorrect key serializer type",  StringSerializer.class, details.getKeySerializerClass());
    }
    
    @Test
    public void testParseKeyAnnotatedProducerClient() throws NoSuchMethodException, SecurityException {
        
        final ProducerDetails details = underTest.parse(mockKeyAnnotatedProducerClientMethodMetadata());
    
        assertEquals("Incorrect topic name",  TEST_TOPIC, details.getTopicName());
        assertEquals("Incorrect client class",  KeyAnnotatedProducerClient.class, details.getClientClass());
        
        final Method expectedMethod = KeyAnnotatedProducerClient.class.getMethod("produceWithKey", String.class, TestObject.class);
        assertEquals("Incorrect producer method",  expectedMethod, details.getProducerMethod());
        
        assertEquals("Incorrect payload class type",  TestObject.class, details.getPayloadClassType());
        assertEquals("Incorrect key class type",  String.class, details.getKeyClassType());
        assertEquals("Incorrect payload serializer class",  JSONSerializer.class, details.getPayloadSerializerClass());
        assertEquals("Incorrect key serializer type",  StringSerializer.class, details.getKeySerializerClass());
    }
    
    private MethodMetadata mockSimpleClientMethodMetadata() {

        return mockMethodMetadata(SimpleKafkaClient.class.getName(), "produce");
    }
    
    private MethodMetadata mockKeyAnnotatedProducerClientMethodMetadata() {
        
        return mockMethodMetadata(KeyAnnotatedProducerClient.class.getName(), "produceWithKey");
    }
    
    private MethodMetadata mockMethodMetadata(String declaringClassName, String producerMethodName) {
        final MethodMetadata metadata = Mockito.mock(MethodMetadata.class);
        
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("topic", TEST_TOPIC);
        
        Mockito.when(metadata.getAnnotationAttributes(KafkaProducer.class.getName())).thenReturn(attributes);
        Mockito.when(metadata.getDeclaringClassName()).thenReturn(declaringClassName);
        Mockito.when(metadata.getMethodName()).thenReturn(producerMethodName);
        return metadata;
    }
    
    @KafkaClient
    private interface KeyAnnotatedProducerClient {
        
        @KafkaProducer(topic = TEST_TOPIC)
        void produceWithKey(@Key String key, @Payload TestObject payload);
    }
}
