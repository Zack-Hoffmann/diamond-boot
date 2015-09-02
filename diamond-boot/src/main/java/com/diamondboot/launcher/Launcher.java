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
package com.diamondboot.launcher;

import com.diamondboot.modules.core.CoreModule;
import com.diamondboot.modules.core.DiamondBootContext;
import com.diamondboot.modules.minecraftserver.proxy.MinecraftServerProxy;
import com.diamondboot.modules.minecraftserver.proxy.MinecraftServerProxyFactory;
import com.diamondboot.modules.minecraftserver.proxy.MinecraftServerProxyModule;
import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstanceManager;
import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstancesModule;
import com.diamondboot.modules.minecraftserver.versions.MinecraftServerVersionsModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class Launcher implements Runnable {

    public static void main(String... args) {
        try {
            String appDir = args.length > 0 ? args[0]
                    : (System.getProperty("user.home") + "/diamond-boot");

            final List allModules = ImmutableList.of(
                    new MinecraftServerProxyModule(),
                    new MinecraftServerVersionsModule(),
                    new MinecraftServerInstancesModule(),
                    new CoreModule(appDir));
            Guice.createInjector(allModules).getInstance(Launcher.class).run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private final DiamondBootContext ctx;
    private final MinecraftServerProxyFactory pxf;
    private final MinecraftServerInstanceManager instMan;

    @Inject
    public Launcher(MinecraftServerProxyFactory pxf, DiamondBootContext ctx, MinecraftServerInstanceManager instMan) {
        this.pxf = pxf;
        this.ctx = ctx;
        this.instMan = instMan;
    }

    @Override
    public void run() {

        final Scanner mainIn = new Scanner(System.in);

        ctx.getStartOnLaunchInstances().stream().forEach(i -> {
            try {
                if (!instMan.getInstance(i).isPresent()) {
                    instMan.newInstance(i);
                }
                final MinecraftServerProxy px = pxf.create(i);
                px.start();

                final Scanner pxIn = new Scanner(px.getInputStream());
                final PrintStream pxOut = new PrintStream(px.getOutputStream());

                // TODO better solution for I/O.  message queues?
                new Thread(() -> {
                    while (px.isRunning()) {
                        if (pxIn.hasNextLine()) {
                            System.out.println("[" + i + "]" + pxIn.nextLine());
                        }
                    }
                }).start();

                new Thread(() -> {
                    while (px.isRunning()) {
                        try {
                            if (System.in.available() > 0 && mainIn.hasNextLine()) {
                                pxOut.println(mainIn.nextLine());
                                pxOut.flush();
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            } catch (IOException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

}
