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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import java.util.List;

public class KafkaSettingsTest {

    @Test
    public void testMultipleBrokerAddresses() {
        final String address1 = "address1:1234";
        final String address2 = "address2:5678";

        final KafkaSettings settings = createKafkaSettings(address1 + "," + address2);

        final List<String> addresses = settings.getBrokerAddresses();

        assertEquals("Address1 incorrect", address1, addresses.get(0));
        assertEquals("Address2 incorrect", address2, addresses.get(1));
    }

    private KafkaSettings createKafkaSettings(String brokerAddresses) {
        return new KafkaSettings(mockEnvironment(brokerAddresses));
    }

    private Environment mockEnvironment(String brokerAddresses) {
        final Environment environment = Mockito.mock(Environment.class);

        Mockito.when(environment.getProperty(eq("kafka.addresses"), any(String.class))).thenReturn(brokerAddresses);

        return environment;
    }
}
