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
import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstanceManager;
import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstanceMetadata;
import com.diamondboot.modules.minecraftserver.versions.MinecraftServerVersionManager;
import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionMetadata;
import com.google.inject.assistedinject.Assisted;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
                "-jar", verJar, "nogui").directory(instMeta.getDir().toFile()).start();
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
