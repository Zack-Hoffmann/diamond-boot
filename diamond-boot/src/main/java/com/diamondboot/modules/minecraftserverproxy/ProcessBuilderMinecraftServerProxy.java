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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ProcessBuilderMinecraftServerProxy implements MinecraftServerProxy {

    private final String baseUrl;
    private final DiamondBootContext ctx;
    private final MinecraftServerVersionManager verMan;
    private final MinecraftServerInstanceManager instMan;
    
    private Process proc = null;

    // TODO will eventually need to create a proxy for each available instance and generate a list of all proxies/instances available
    
    @Inject
    public ProcessBuilderMinecraftServerProxy(
            DiamondBootContext ctx,
            @Named("mcVersionsBaseUrl") String baseUrl,
            MinecraftServerVersionManager verMan,
            MinecraftServerInstanceManager instMan) {
        this.ctx = ctx;
        this.baseUrl = baseUrl;
        this.verMan = verMan;
        this.instMan = instMan;

    }

    @Override
    public void start() throws IOException {

        if (Files.notExists(ctx.getMinecraftVersionsDirectory())) {
            Files.createDirectories(ctx.getMinecraftVersionsDirectory());
        }

        MinecraftVersionMetadata meta = verMan.getLatestVersion();
        String ver = meta.getId();

        String fileName = "/minecraft_server." + ver + ".jar";
        Path jarFile = Paths.get(ctx.getMinecraftVersionsDirectory().toString() + fileName);
        String fullDownloadUrl = baseUrl + ver + fileName;

        if (Files.notExists(jarFile)) {
            HttpURLConnection con = (HttpURLConnection) new URL(fullDownloadUrl).openConnection();

            try (
                    ReadableByteChannel rc = Channels.newChannel(con.getInputStream());
                    FileChannel fc = FileChannel.open(jarFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                fc.transferFrom(rc, 0, 10000000);
            }
        }

        if (Files.notExists(ctx.getMinecraftInstancesDirectory())) {
            Files.createDirectories(ctx.getMinecraftInstancesDirectory());
        }

        // TODO check context for default instances, create them if they don't exist (using default values), then run them
        MinecraftServerInstanceMetadata instMeta = instMan.getInstances().get(0);
        Path instanceDir = Paths.get(ctx.getMinecraftInstancesDirectory().toString() + "/" + instMeta.getId());
        Files.createDirectories(instanceDir);

        proc = new ProcessBuilder(
                "java",
                "-Xmx" + instMeta.getMaxMemory(),
                "-Xms" + instMeta.getInitialMemory(),
                "-jar", jarFile.toString(), "nogui")
                .inheritIO().directory(instanceDir.toFile()).start();
    }

}
