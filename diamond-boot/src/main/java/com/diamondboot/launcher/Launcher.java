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
import com.diamondboot.modules.core.DiamondBootConsole;
import com.diamondboot.modules.core.DiamondBootContext;
import com.diamondboot.modules.events.EventBus;
import com.diamondboot.modules.events.EventsModule;
import com.diamondboot.modules.minecraftserver.proxy.MinecraftProxyModule;
import com.diamondboot.modules.minecraftserver.instances.MinecraftInstanceManager;
import com.diamondboot.modules.minecraftserver.instances.MinecraftInstancesModule;
import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionModule;
import com.diamondboot.modules.web.DiamondBootWebServer;
import com.diamondboot.modules.web.ServletsModule;
import com.diamondboot.modules.web.WebServerModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import java.io.IOException;
import java.util.List;
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

            final List allModules = ImmutableList.of(new MinecraftProxyModule(),
                    new MinecraftVersionModule(),
                    new MinecraftInstancesModule(),
                    new EventsModule(),
                    new WebServerModule(),
                    new ServletsModule(),
                    new CoreModule(appDir));
            Guice.createInjector(allModules).getInstance(Launcher.class).run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private final DiamondBootContext ctx;
    private final MinecraftInstanceManager instMan;
    private final EventBus eventBus;
    private final DiamondBootConsole con;
    private final DiamondBootWebServer webServ;

    @Inject
    public Launcher(EventBus eventBus,
            DiamondBootContext ctx,
            MinecraftInstanceManager instMan,
            DiamondBootConsole con,
            DiamondBootWebServer webServ) {
        this.ctx = ctx;
        this.instMan = instMan;
        this.eventBus = eventBus;
        this.con = con;
        this.webServ = webServ;
    }

    @Override
    public void run() {
        con.start();
        eventBus.start();

        ctx.getStartOnLaunchInstances().stream().forEach(i -> {
            try {
                instMan.startInstance(i);
            } catch (IOException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        webServ.start();

    }

}
