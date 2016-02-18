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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.integration.kafka.core.BrokerAddress;
import org.springframework.integration.kafka.core.Configuration;
import org.springframework.integration.kafka.core.DefaultConnectionFactory;

import com.capgemini.gregor.internal.KafkaSettings;

public class KafkaConsumersConfigurationTest {

    private static final String BROKER_ADDRESS = "kafka.url:1234";
    
    private KafkaConsumersConfiguration underTest = new KafkaConsumersConfiguration();
    
    @Test
    public void testKafkaBrokerConfiguration() {
        final Configuration config = underTest.kafkaBrokerConfiguration(createSettings());
        
        assertCorrectBrokerAddress(config);
    }
    
    @Test
    public void testKafkaBrokerConnectionFactory() throws Exception {
        final DefaultConnectionFactory connectionFactory = (DefaultConnectionFactory) underTest.kafkaBrokerConnectionFactory(createSettings());
        
        assertCorrectBrokerAddress(connectionFactory.getConfiguration());
    }
    
    @Test public void testTest() {
        final KafkaSettings settings = underTest.kafkaSettings(createEnvironment());
    
        assertEquals("Broker address incorrect", BROKER_ADDRESS, settings.getBrokerAddress());
    }
    
    private void assertCorrectBrokerAddress(Configuration configuration) {
        assertEquals("Broker address list is wrong size", 1, configuration.getBrokerAddresses().size());
        
        final BrokerAddress address = configuration.getBrokerAddresses().get(0);
        
        assertEquals("Incorrect broker address", BROKER_ADDRESS, address.toString());
    }
    
    private Environment createEnvironment() {
        final Environment environment = mock(Environment.class);
        
        when(environment.getProperty(eq("kafka.address"), isA(String.class))).thenReturn(BROKER_ADDRESS);
   
        return environment;
    }
    
    private KafkaSettings createSettings() {
        final KafkaSettings mockSettings = mock(KafkaSettings.class);
        when(mockSettings.getBrokerAddress()).thenReturn(BROKER_ADDRESS);
        
        return mockSettings;
    }
}
