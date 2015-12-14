/*
 * Copyright 2015 Zack Hoffmann <zachary.hoffmann@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diamondboot.modules.events;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.inject.Singleton;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class EventsModule extends AbstractModule {

    @Override
    public void configure() {
        bind(EventBus.class).to(AsyncEventBus.class);
    }

    @Provides
    @Singleton
    Executor providesExecutor() {
        // TODO make event thread pool configurable
        return Executors.newFixedThreadPool(10);
    }

    @Provides
    @Singleton
    AsyncEventBus providesAsyncEventBus(Executor executor) {
        return new AsyncEventBus(executor);
    }

}
