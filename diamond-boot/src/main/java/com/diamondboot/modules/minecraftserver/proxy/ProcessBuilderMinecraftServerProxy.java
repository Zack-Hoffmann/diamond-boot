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
package com.diamondboot.modules.minecraftserver.proxy;

import com.diamondboot.modules.core.DiamondBootContext;
import com.diamondboot.modules.events.DiamondBootServerEventReceiver;
import com.diamondboot.modules.events.MinecraftServerEvent;
import com.diamondboot.modules.events.MinecraftServerEventPublisher;
import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstanceManager;
import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstanceMetadata;
import com.diamondboot.modules.minecraftserver.versions.MinecraftServerVersionManager;
import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionMetadata;
import com.google.inject.assistedinject.Assisted;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ProcessBuilderMinecraftServerProxy implements MinecraftServerProxy {

    private final String instance;
    private final DiamondBootContext ctx;
    private final MinecraftServerVersionManager verMan;
    private final MinecraftServerInstanceManager instMan;
    private final MinecraftServerEventPublisher eventPub;
    private final DiamondBootServerEventReceiver eventRec;

    private Process proc = null;

    @Inject
    public ProcessBuilderMinecraftServerProxy(
            DiamondBootContext ctx,
            MinecraftServerVersionManager verMan,
            MinecraftServerInstanceManager instMan,
            @Assisted String instance,
            MinecraftServerEventPublisher eventPub,
            DiamondBootServerEventReceiver eventRec) {
        this.ctx = ctx;
        this.verMan = verMan;
        this.instMan = instMan;
        this.instance = instance;
        this.eventPub = eventPub;
        this.eventRec = eventRec;
    }

    @Override
    public void start() throws IOException {

        MinecraftServerInstanceMetadata instMeta = instMan.getInstance(instance).get();

        String verStr = instMeta.getVersionMetadata().getId();
        Optional<MinecraftVersionMetadata> version = verMan.getInstalledVersion(verStr);

        if (!version.isPresent()) {
            verMan.installVersion(verStr);
        }

        String verJar = verMan.getInstalledVersion(verStr).get().getJarFile().get().toString();

        proc = new ProcessBuilder(
                "java",
                "-Xmx" + instMeta.getMaxMemory(),
                "-Xms" + instMeta.getInitialMemory(),
                "-jar", verJar, "nogui").directory(instMeta.getDir().toFile()).start();

        new Thread(() -> {
            try (Scanner pxIn = new Scanner(getInputStream())) {
                while (isRunning()) {
                    if (pxIn.hasNextLine()) {
                        eventPub.publish(MinecraftServerEvent.newInstance(instMeta, pxIn.nextLine()));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ProcessBuilderMinecraftServerProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        eventRec.addListener(e -> {
            try (PrintStream pxOut = new PrintStream(getOutputStream());) {
                if (instMeta.getId().equals(e.getTargetInstance())) {
                    pxOut.println(e.getContent());
                    pxOut.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(ProcessBuilderMinecraftServerProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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

}
