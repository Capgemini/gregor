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
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.capgemini.gregor.DecodingException;
import com.capgemini.gregor.TestObject;

public class JSONDecoderTest {

    private JSONDecoder underTest;
    
    private static final String TEST_OBJECT_JSON = "{\"stringValue\" : \"Test String\", \"intValue\" : 1234, \"booleanValue\" : true, \"listValue\" : [\"One\", \"Two\", \"Three\"], \"mapValue\" : {\"Key1\" : \"Value1\", \"Key2\" : \"Value2\"}}";
    
    private static final String TEST_OBJECT_JSON_INVALID = "{\"stringValue\" : \"Test String\", \"intValue\" : 1234, \"booleanValue\" : true, \"listValue\" : [\"One\", \"Two\", \"Three\"], \"mapValue\" : {\"Key1\" : \"Value1\", \"Key2\" : \"Value2\"";
    
    @Before
    public void init() {
        underTest = new JSONDecoder();
    }
    
    @Test
    public void testFromBytesValidJSON() {
        underTest.setType(TestObject.class);
        final TestObject testObject = (TestObject)underTest.fromBytes(TEST_OBJECT_JSON.getBytes());
    
        assertThatDecodedTestObjectIsCorrect(testObject);
    }
    
    @Test(expected = DecodingException.class)
    public void testFromBytesInvalidJSON() {
        underTest.setType(TestObject.class);
        underTest.fromBytes(TEST_OBJECT_JSON_INVALID.getBytes());
    }

    @Test
    public void testFromBytesNull() {
        underTest.setType(TestObject.class);
        assertNull(underTest.fromBytes(null));
    }
    
    @Test(expected = DecodingException.class)
    public void testFromBytesTypeNotSet() {
        underTest.fromBytes(TEST_OBJECT_JSON.getBytes());
    }

    private void assertThatDecodedTestObjectIsCorrect(TestObject testObject) {
        assertEquals("stringValue not correct", "Test String", testObject.getStringValue());
        assertEquals("intValue not correct", 1234, testObject.getIntValue());
        assertEquals("booleanValue not correct", true, testObject.isBooleanValue());
        assertEquals("listValue not correct", true, Arrays.equals(testObject.getListValue().toArray(), new String[]{"One", "Two", "Three"}));
        assertEquals("mapValue keys incorrect", true, Arrays.equals(testObject.getMapValue().keySet().toArray(), new String[]{"Key1", "Key2"}));
        assertEquals("mapValue values incorrect", true, Arrays.equals(testObject.getMapValue().values().toArray(), new String[]{"Value1", "Value2"}));
    }
}
