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
package com.diamondboot.modules.core;

import com.diamondboot.modules.events.DiamondBootEvent;
import com.diamondboot.modules.events.DiamondBootEventPublisher;
import com.diamondboot.modules.events.MinecraftServerEvent;
import com.diamondboot.modules.events.MinecraftEventReceiver;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class DiamondBootConsole {

    private boolean running = false;
    private final Scanner sc = new Scanner(System.in);
    private final MinecraftEventReceiver eventRec;
    private final DiamondBootEventPublisher eventPub;

    @Inject
    public DiamondBootConsole(MinecraftEventReceiver eventRec,
            DiamondBootEventPublisher eventPub) {
        this.eventRec = eventRec;
        this.eventPub = eventPub;
    }

    public void start() {
        eventRec.addListener((MinecraftServerEvent e) -> {
            System.out.println("[" + e.getInstanceMetadata().getId() + "]" + e.getContent());
        });

        new Thread(() -> {
            while (isRunning()) {
                try {
                    if (System.in.available() > 0 && sc.hasNextLine()) {
                        eventPub.publish(DiamondBootEvent.newAllInstanceEvent(sc.nextLine()));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DiamondBootConsole.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        running = true;
    }

    public void stop() {
        running = false;
    }

    public final boolean isRunning() {
        return running;
    }

}
