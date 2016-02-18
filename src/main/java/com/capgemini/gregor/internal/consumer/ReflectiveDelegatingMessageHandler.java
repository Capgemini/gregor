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

import java.lang.reflect.Method;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * A message handler that delegates execution to a method of a class via reflection.
 * 
 * @author craigwilliams84
 *
 */
public class ReflectiveDelegatingMessageHandler implements MessageHandler {

    private Object delegate;
    
    private Method delegateMethod;
    
    private Class<?> delegateMethodArgType;
    
    public ReflectiveDelegatingMessageHandler(Object delegate, String delegateMethodName, Class<?> delegateMethodArgType) throws NoSuchMethodException, SecurityException {
        this.delegate = delegate;
        this.delegateMethodArgType = delegateMethodArgType;
        delegateMethod = delegate.getClass().getMethod(delegateMethodName, delegateMethodArgType);
    }
    
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        final Object payload = message.getPayload();
        
        if (payload.getClass().isAssignableFrom(delegateMethodArgType)) {
            try {
                delegateMethod.invoke(delegate, payload);
            } catch (Exception e) {
                throw new MessagingException("Unable to invoke delegate handler", e);
            }

        } else {
            throw new MessagingException("Payload type: " + payload.getClass().getName() 
                    + " is incompatible with delegate method argument " + delegateMethodArgType.getName());
        }
        
    }
}
