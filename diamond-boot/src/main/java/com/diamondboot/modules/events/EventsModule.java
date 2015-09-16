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

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class EventsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(InMemoryGlobalEventBus.class).in(Scopes.SINGLETON);
        bind(MinecraftServerEventReceiver.class).to(InMemoryGlobalEventBus.class);
        bind(MinecraftServerEventPublisher.class).to(InMemoryGlobalEventBus.class);
        bind(DiamondBootServerEventReceiver.class).to(InMemoryGlobalEventBus.class);
        bind(DiamondBootServerEventPublisher.class).to(InMemoryGlobalEventBus.class);
        bind(EventBus.class).to(InMemoryGlobalEventBus.class);
    }

}
