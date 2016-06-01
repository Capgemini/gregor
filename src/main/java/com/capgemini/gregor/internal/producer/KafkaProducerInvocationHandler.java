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
import java.util.concurrent.Future;

import com.capgemini.gregor.KafkaProducerCallback;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.JdkFutureAdapters;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.record.Record;

public class KafkaProducerInvocationHandler implements InvocationHandler {

    private Producer<?,?> producer;
    
    private ProducerDetails details;
    
    public KafkaProducerInvocationHandler(Producer<?,?> producer, ProducerDetails details) {
        this.producer = producer;
        this.details = details;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        final KafkaProducerCallback callback = getCallback(method, args);

        final Future<RecordMetadata> future = producer.send(new ProducerRecord(details.getTopicName(), args[0]));

        if (callback != null) {
            wireUpCallback(future, callback);
        }
        return null;
    }

    private KafkaProducerCallback getCallback(Method method, Object[] args) {
        for (Object arg : args) {
            if (arg instanceof KafkaProducerCallback) {
                return (KafkaProducerCallback) arg;
            }
        }

        return null;
    }

    private void wireUpCallback(final Future<RecordMetadata> future, final KafkaProducerCallback callback) {
        final ListenableFuture<RecordMetadata> listenableFuture = JdkFutureAdapters.listenInPoolThread(future);
        //TODO threadpool
        Futures.addCallback(listenableFuture, new FutureCallback<RecordMetadata>(){

            @Override
            public void onSuccess(RecordMetadata metadata) {
                callback.onSuccess(metadata);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }
}
