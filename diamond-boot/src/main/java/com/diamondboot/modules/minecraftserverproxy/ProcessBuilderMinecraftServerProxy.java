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
package com.diamondboot.modules.minecraftserverproxy;

import com.diamondboot.modules.core.DiamondBootContext;
import com.diamondboot.modules.minecraftserverproxy.instances.MinecraftServerInstanceManager;
import com.diamondboot.modules.minecraftserverproxy.instances.MinecraftServerInstanceMetadata;
import com.diamondboot.modules.minecraftserverproxy.versions.MinecraftServerVersionManager;
import com.diamondboot.modules.minecraftserverproxy.versions.MinecraftVersionMetadata;
import com.google.inject.assistedinject.Assisted;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
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

    private Process proc = null;

    // TODO will eventually need to create a proxy for each available instance and generate a list of all proxies/instances available
    @Inject
    public ProcessBuilderMinecraftServerProxy(
            DiamondBootContext ctx,
            MinecraftServerVersionManager verMan,
            MinecraftServerInstanceManager instMan,
            @Assisted String instance) {
        this.ctx = ctx;
        this.verMan = verMan;
        this.instMan = instMan;
        this.instance = instance;
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
                "-jar", verJar, "nogui")
                .inheritIO().directory(instMeta.getDir().toFile()).start();
    }

}
