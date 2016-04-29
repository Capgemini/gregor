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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.integration.kafka.core.BrokerAddress;
import org.springframework.integration.kafka.core.BrokerAddressListConfiguration;
import org.springframework.integration.kafka.core.ConnectionFactory;
import org.springframework.integration.kafka.core.DefaultConnectionFactory;

import com.capgemini.gregor.internal.KafkaCommonConfiguration;
import com.capgemini.gregor.internal.KafkaSettings;

import java.util.List;

/**
 * Configuration to create beans where one instance is required for both kafka consumers and producers
 * 
 * @author craigwilliams84
 *
 */
@Configuration
@ConditionalOnMissingBean(ConnectionFactory.class)
public class KafkaConsumersConfiguration extends KafkaCommonConfiguration {
    
    public static final String KAFKA_CONNECTION_FACTORY_BEAN_NAME = "kafkaBrokerConnectionFactory";
        
    @Bean(name = KAFKA_CONNECTION_FACTORY_BEAN_NAME)
    public ConnectionFactory kafkaBrokerConnectionFactory(org.springframework.integration.kafka.core.Configuration config) throws Exception {
        return new DefaultConnectionFactory(config);
    }
    
    @Bean
    public org.springframework.integration.kafka.core.Configuration kafkaBrokerConfiguration(KafkaSettings config) {
        final BrokerAddressListConfiguration configuration = new BrokerAddressListConfiguration(
                getBrokerAddresses(config));

        configuration.setSocketTimeout(500);
        return configuration;
    }

    private BrokerAddress[] getBrokerAddresses(KafkaSettings config) {
        final List<String> brokerAddressesStrings = config.getBrokerAddresses();

        final BrokerAddress[] brokerAddresses = new BrokerAddress[brokerAddressesStrings.size()];

        for (int i  = 0; i < brokerAddressesStrings.size(); i++) {
            brokerAddresses[i] = BrokerAddress.fromAddress(brokerAddressesStrings.get(i));
        }

        return brokerAddresses;
    }
}
