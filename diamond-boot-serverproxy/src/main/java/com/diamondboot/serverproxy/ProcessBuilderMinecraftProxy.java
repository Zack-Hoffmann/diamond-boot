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
package com.diamondboot.serverproxy;

import com.diamondboot.core.event.DiamondBootEvent;
import com.diamondboot.core.event.MinecraftServerEvent;
import com.diamondboot.core.metadata.MinecraftInstanceMetadata;
import com.diamondboot.core.metadata.MinecraftVersionMetadata;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ProcessBuilderMinecraftProxy implements MinecraftProxy {
    
    private final EventBus bus;
    private final MinecraftInstanceMetadata instMeta;
    private final MinecraftVersionMetadata versMeta;
    
    private Process proc = null;
    
    public ProcessBuilderMinecraftProxy(
            MinecraftVersionMetadata versMeta,
            MinecraftInstanceMetadata instMeta,
            EventBus bus) throws IOException {
        this.bus = bus;
        this.instMeta = instMeta;
        this.versMeta = versMeta;
    }
    
    @Override
    public void start() throws IOException {
        
        String verJar = versMeta.getJarFileStr();
        
        proc = new ProcessBuilder(
                "java",
                "-Xmx" + instMeta.getMaxMemory(),
                "-Xms" + instMeta.getInitialMemory(),
                "-jar", verJar, "nogui").directory(instMeta.getDir().toFile()).start();
        
        new Thread(() -> {
            try (Scanner pxIn = new Scanner(getInputStream())) {
                while (isRunning()) {
                    if (pxIn.hasNextLine()) {
                        bus.post(MinecraftServerEvent.newInstance(instMeta, pxIn.nextLine()));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ProcessBuilderMinecraftProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
        
        bus.register(this);
    }
    
    @Subscribe
    public void outputEventToProcess(DiamondBootEvent e) {
        try (PrintStream pxOut = new PrintStream(getOutputStream());) {
            if (instMeta.getId().equals(e.getTargetInstance())) {
                pxOut.println(e.getContent());
                pxOut.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(ProcessBuilderMinecraftProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return proc != null ? proc.getInputStream() : null;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        return proc != null ? proc.getOutputStream() : null;
    }
    
    @Override
    public boolean isRunning() {
        return proc.isAlive();
    }
    
    @Override
    public void stop() throws IOException {
        try (PrintStream pxOut = new PrintStream(getOutputStream());) {
            pxOut.println("stop");
            pxOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(ProcessBuilderMinecraftProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
