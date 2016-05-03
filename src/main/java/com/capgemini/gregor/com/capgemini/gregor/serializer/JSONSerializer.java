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

package com.capgemini.gregor.com.capgemini.gregor.serializer;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * JSON Kafka Serializer
 *
 * @author craigwilliams84
 */
public class JSONSerializer implements Serializer {

    private Gson gson;

    @Override
    public void configure(Map map, boolean b) {
        gson = new Gson();
    }

    @Override
    public byte[] serialize(String topic, Object object) {
        return gson.toJson(object).getBytes();
    }

    @Override
    public void close() {
        //DO NOTHING
    }
}
