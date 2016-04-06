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

package com.capgemini.gregor.internal.producer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.capgemini.gregor.GregorException;

/**
 * Default implementation of a DelegatingInvocationHandler.
 * 
 * @author craigwilliams84
 *
 */
public class DefaultDelegatingInvocationHandler implements DelegatingInvocationHandler {

    private Map<Method, InvocationHandler> delegates = new HashMap<Method, InvocationHandler>();
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        final InvocationHandler delegate = delegates.get(method);
        
        if (delegate == null) {
            throw new GregorException("Unable to execute method, " + method.getName() 
                    + " because no delegate is set.  Something bad has happened!");
        }
        
        return delegate.invoke(proxy, method, args);
    }

    @Override
    public void addDelegateHandler(Method method, InvocationHandler delegate) {
        delegates.put(method, delegate);       
    }

}
