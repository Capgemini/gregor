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

import org.apache.log4j.Logger;

import com.capgemini.gregor.DecodingException;
import com.capgemini.gregor.TypeSettableDecoder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JSONDecoder implements TypeSettableDecoder {

    private static final Logger LOG = Logger.getLogger(JSONDecoder.class);
    private static final Gson GSON = new Gson();
    
    private Class<?> type;
    
    public JSONDecoder() {
        
    }
    
    @Override
    public Object fromBytes(byte[] bytes) {
        if (bytes == null) {
            LOG.warn("Received null bytes, returning null");
            
            return null;
        }
        
        if (type == null) {
            throw new DecodingException("Unable to decode JSON as the type is not set on the decoder");
        }
        
        try {
            return GSON.fromJson(new String(bytes), type);
        } catch (JsonSyntaxException e) {
            throw new DecodingException("Unable to decode JSON", e);
        }
    }

    @Override
    public void setType(Class<?> type) {
        this.type = type;
        
    }

}
