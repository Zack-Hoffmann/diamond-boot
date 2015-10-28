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

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class InMemoryGlobalEventBus implements EventBus {

    private final List<MinecraftEventListener> mcServerEventListeners = Lists.newArrayList();
    private final Queue<MinecraftServerEvent> mcServerEventQueue = Queues.newConcurrentLinkedQueue();
    private final List<DiamondBootEventListener> dbServerEventListeners = Lists.newArrayList();
    private final Queue<DiamondBootEvent> dbServerEventQueue = Queues.newConcurrentLinkedQueue();
    private boolean running = true;

    @Override
    public void publish(MinecraftServerEvent e) {
        mcServerEventQueue.add(e);
    }

    @Override
    public void addListener(MinecraftEventListener l) {
        mcServerEventListeners.add(l);
    }

    @Override
    public void publish(DiamondBootEvent e) {
        dbServerEventQueue.add(e);
    }

    @Override
    public void addListener(DiamondBootEventListener l) {
        dbServerEventListeners.add(l);
    }

    @Override
    public void start() {
        new Thread(() -> {
            while (isRunning()) {
                final MinecraftServerEvent e = mcServerEventQueue.poll();
                if (e != null) {
                    mcServerEventListeners.stream().forEach(l -> l.onMinecraftServerEvent(e));
                }
            }
        }).start();

        new Thread(() -> {
            while (isRunning()) {
                final DiamondBootEvent e = dbServerEventQueue.poll();
                if (e != null) {
                    dbServerEventListeners.stream().forEach(l -> l.onDiamondBootServerEvent(e));
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public final boolean isRunning() {
        return running;
    }

}
