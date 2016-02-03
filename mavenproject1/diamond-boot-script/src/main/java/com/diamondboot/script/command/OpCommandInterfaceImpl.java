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
package com.diamondboot.script.command;

import com.diamondboot.core.event.DiamondBootEvent;
import com.diamondboot.core.event.MinecraftServerEvent;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class OpCommandInterfaceImpl implements OpCommandInterface {

    private final String instance;
    private final EventBus bus;
    private final BlockingQueue<String> responses = new LinkedBlockingQueue<>();

    public OpCommandInterfaceImpl(String instance, EventBus bus) {
        this.instance = instance;
        this.bus = bus;
    }

    public void invoke(String command, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(command);
        for (String a : args) {
            sb.append(" ").append(a);
        }
        bus.post(DiamondBootEvent.newEvent(instance, sb.toString()));
    }

    @Subscribe
    public void queueResponses(MinecraftServerEvent e) {
        if (e.getInstanceMetadata().getId().equals(instance)) {
            responses.add(e.getContent());
        }
    }

    @Override
    public List<String> list() throws InterruptedException {
        List<String> players = Lists.newArrayList();
        bus.register(this);
        invoke("list");
        int count = -1;
        do {
            String line = responses.take();
            // TODO log
            Pattern p = Pattern.compile("There are ([\\d]+)/[\\d]+ players online");
            Matcher m = p.matcher(line);
            if (m.find()) {
                count = Integer.parseInt(m.group(1));
            }
        } while (count == -1);
        for (int i = 0; i < count; i++) {
            String line = responses.take();
            Pattern p = Pattern.compile(": ([\\w]+)$");
            Matcher m = p.matcher(line);
            if (m.find()) {
                players.add(m.group(1));
            }
        }
        bus.unregister(this);
        return players;
    }

    @Override
    public List<String> banlist(String option) {
        // TODO implement
        return Lists.newArrayList();
    }

    @Override
    public Long time(String option, String value) {
        // TODO implement
        return 0L;
    }

    @Override
    public int maxPlayers() throws InterruptedException {
        int max = -1;
        bus.register(this);
        invoke("list");
        boolean done = false;
        do {
            String line = responses.take();
            // TODO log
            Pattern p = Pattern.compile("There are [\\d]+/([\\d]+) players online");
            Matcher m = p.matcher(line);
            if (m.find()) {
                max = Integer.parseInt(m.group(1));
                done = true;
            }
        } while (!done);
        bus.unregister(this);
        return max;
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
