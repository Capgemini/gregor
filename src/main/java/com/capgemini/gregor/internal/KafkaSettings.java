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

package com.capgemini.gregor.internal;

import org.springframework.core.env.Environment;

/**
 * Encapsulates Kafka instance configuration properties.
 * 
 * @author craigwilliams84
 *
 */

public class KafkaSettings {
    
    private static final String BROKER_ADDRESS_PROPERTY = "kafka.address";
    private static final String BROKER_ADDRESS_DEFAULT = "localhost:9092";
    
    private static final String ZOOKEEPER_ADDRESS_PROPERTY = "zookeeper.address";
    private static final String ZOOKEEPER_ADDRESS_DEFAULT = "localhost:2181";

    private Environment environment;

    public KafkaSettings(Environment environment) {
        this.environment = environment;
    }

    public String getBrokerAddress() {
        return getSetting(BROKER_ADDRESS_PROPERTY, BROKER_ADDRESS_DEFAULT);
    }

    public String getZookeeperAddress() {
        return getSetting(ZOOKEEPER_ADDRESS_PROPERTY, ZOOKEEPER_ADDRESS_DEFAULT);
    }
    
    private String getSetting(String property, String defaultValue) {
        return environment.getProperty(property, defaultValue);
    }
}
