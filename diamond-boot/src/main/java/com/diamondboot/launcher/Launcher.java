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

import com.diamondboot.core.DiamondBootContext;
import com.diamondboot.modules.core.CoreModule;
import com.diamondboot.modules.core.DiamondBootConsole;
import com.diamondboot.modules.events.EventsModule;
import com.diamondboot.modules.minecraftserver.commands.CommandModule;
import com.diamondboot.modules.minecraftserver.proxy.MinecraftProxyModule;
import com.diamondboot.modules.minecraftserver.instances.MinecraftInstancesModule;
import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionModule;
import com.diamondboot.modules.status.StatusModule;
import com.diamondboot.web.DiamondBootWebServer;
import com.diamondboot.modules.web.ServletsModule;
import com.diamondboot.modules.web.WebServerModule;
import com.diamondboot.serverproxy.MinecraftProxyFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
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
    
    public static final String DIAMOND_BOOT_VERSION = "alpha 1";

    public static void main(final String... args) {
        final Logger l = Logger.getLogger("BootstrapLogger");
        l.log(Level.INFO, "Configuring Diamond Boot.");
        
        try {
            String appDir = args.length > 0 ? args[0]
                    : (System.getProperty("user.home") + "/diamond-boot");
            l.log(Level.INFO, "Application directory is {0}.", appDir);
            l.log(Level.INFO, "Creating modules.  Adjust logging to CONFIG for more information.");

            final List allModules = ImmutableList.of(new MinecraftProxyModule(),
                    new MinecraftVersionModule(),
                    new MinecraftInstancesModule(),
                    new EventsModule(),
                    new WebServerModule(),
                    new ServletsModule(),
                    new StatusModule(),
                    new CommandModule(),
                    new CoreModule(appDir));
            l.log(Level.INFO, "Launching!");
            Guice.createInjector(allModules).getInstance(Launcher.class).run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private final Logger log;
    private final DiamondBootContext ctx;
    private final DiamondBootConsole con;
    private final DiamondBootWebServer webServ;
    private final MinecraftProxyFactory factory;

    @Inject
    public Launcher(Logger log,
            EventBus eventBus,
            DiamondBootContext ctx,
            DiamondBootConsole con,
            DiamondBootWebServer webServ,
            MinecraftProxyFactory factory) {
        this.log = log;
        this.ctx = ctx;
        this.con = con;
        this.webServ = webServ;
        this.factory = factory;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "Starting console.");
        con.start();
        
        log.log(Level.INFO, "Starting instances.");
        ctx.getStartOnLaunchInstances().stream().forEach(i -> {
            log.log(Level.INFO, "Starting instance \"{0}\".", i);
            try {
                factory.get(i).start();
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        });
        
        log.log(Level.INFO, "Starting web server.");
        webServ.start();

    }

}
