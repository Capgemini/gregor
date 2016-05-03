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

package com.capgemini.gregor.serializer;

import com.capgemini.gregor.TestObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JSONSerializerTest {

    @Test
    public void testSerialize() {
        final JSONSerializer serializer = new JSONSerializer();
        serializer.configure(null, true);

        final TestObject testObject = new TestObject();
        testObject.setStringValue("Testing Testing 123");
        testObject.setIntValue(1234);
        testObject.setBooleanValue(false);
        testObject.setListValue(Arrays.asList("Hey", "Mr", "Tambourine", "Man"));
        final Map<String, String> map = new HashMap<String, String>();
        map.put("Key", "Value");
        testObject.setMapValue(map);

        String jsonString = new String(serializer.serialize("topic", testObject));

        //Remove spaces
        jsonString = jsonString.replace(" ", "");

        assertEquals("Serialized json incorrect",
                "{\"stringValue\":\"TestingTesting123\"," +
                "\"intValue\":1234," +
                "\"booleanValue\":false," +
                "\"listValue\":[\"Hey\",\"Mr\",\"Tambourine\",\"Man\"]," +
                "\"mapValue\":{\"Key\":\"Value\"}}", jsonString);
    }
}

