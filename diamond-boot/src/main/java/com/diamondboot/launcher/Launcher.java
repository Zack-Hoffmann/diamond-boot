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
import com.diamondboot.modules.minecraftserverproxy.MinecraftServerProxyFactory;
import com.diamondboot.modules.minecraftserverproxy.MinecraftServerProxyModule;
import com.diamondboot.modules.minecraftserverproxy.instances.MinecraftServerInstanceManager;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import java.io.IOException;
import java.util.Arrays;
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

            final List allModules = ImmutableList.of(
                    new MinecraftServerProxyModule(appDir),
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

        Arrays.asList(ctx.getAppProperties().getProperty("instances.startOnLaunch").split(",")).stream().forEach(i -> {
            try {
                if (!instMan.getInstance(i).isPresent()) {
                    instMan.newInstance(i);
                }
                pxf.create(i).start();
            } catch (IOException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

}
