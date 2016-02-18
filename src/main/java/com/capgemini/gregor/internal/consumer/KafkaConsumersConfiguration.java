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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.integration.kafka.core.BrokerAddress;
import org.springframework.integration.kafka.core.BrokerAddressListConfiguration;
import org.springframework.integration.kafka.core.ConnectionFactory;
import org.springframework.integration.kafka.core.DefaultConnectionFactory;

import com.capgemini.gregor.internal.KafkaSettings;

/**
 * Configuration to create beans that are common for all kafka consumers
 * 
 * @author craigwilliams84
 *
 */
@Configuration
public class KafkaConsumersConfiguration {
    
    static final String KAFKA_CONNECTION_FACTORY_BEAN_NAME = "kafkaBrokerConnectionFactory";
    
    @Bean(name = KAFKA_CONNECTION_FACTORY_BEAN_NAME)
    public ConnectionFactory kafkaBrokerConnectionFactory(KafkaSettings config) throws Exception {
        return new DefaultConnectionFactory(kafkaBrokerConfiguration(config));
    }
    
    @Bean
    public org.springframework.integration.kafka.core.Configuration kafkaBrokerConfiguration(KafkaSettings config) {
        BrokerAddressListConfiguration configuration = new BrokerAddressListConfiguration(
                BrokerAddress.fromAddress(config.getBrokerAddress()));
        configuration.setSocketTimeout(500);
        return configuration;
    }
    
    @Bean 
    public KafkaSettings kafkaSettings(Environment environment) {
        return new KafkaSettings(environment);
    }
    
}
