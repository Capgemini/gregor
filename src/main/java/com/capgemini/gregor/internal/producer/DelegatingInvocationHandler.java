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

/**
 * An invocation handler that invokes a registered delegate handler, dependent on the method.
 * 
 * @author craigwilliams84
 *
 */
public interface DelegatingInvocationHandler extends InvocationHandler {

    /**
     * Register a delegate invocation handler for the specified method.
     * 
     * @param method The method that this delegate should be invoked for.
     * @param delegate The delegate invocation handler
     */
    void addDelegateHandler(Method method, InvocationHandler delegate);
}
