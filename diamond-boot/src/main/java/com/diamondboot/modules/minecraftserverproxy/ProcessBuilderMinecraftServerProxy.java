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

    private final Path baseDir;
    private final Path mcVersions;
    private final Path mcInstances;
    private final String baseUrl;
    private final MinecraftServerVersionManager verMan;
    private final MinecraftServerInstanceManager instMan;

    @Inject
    public ProcessBuilderMinecraftServerProxy(
            @Named("baseDir") String baseDir,
            @Named("mcVersionsDirectory") String mcVersionsDirectory,
            @Named("mcInstancesDirectory") String mcInstancesDirectory,
            @Named("mcVersionsBaseUrl") String baseUrl,
            MinecraftServerVersionManager verMan,
            MinecraftServerInstanceManager instMan) {
        this.baseDir = Paths.get(baseDir);
        this.mcVersions = Paths.get(baseDir + "/" + mcVersionsDirectory);
        this.mcInstances = Paths.get(baseDir + "/" + mcInstancesDirectory);
        this.baseUrl = baseUrl;
        this.verMan = verMan;
        this.instMan = instMan;

    }

    @Override
    public void start() throws IOException {

        Files.createDirectories(baseDir);
        Files.createDirectories(mcVersions);
        Files.createDirectories(mcInstances);

        MinecraftVersionMetadata meta = verMan.getLatestVersion();
        String ver = meta.getId();

        String fileName = "/minecraft_server." + ver + ".jar";
        Path jarFile = Paths.get(mcVersions.toString() + fileName);
        String fullDownloadUrl = baseUrl + ver + fileName;

        if (Files.notExists(jarFile)) {
            HttpURLConnection con = (HttpURLConnection) new URL(fullDownloadUrl).openConnection();

            try (
                    ReadableByteChannel rc = Channels.newChannel(con.getInputStream());
                    FileChannel fc = FileChannel.open(jarFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                fc.transferFrom(rc, 0, 10000000);
            }
        }

        MinecraftServerInstanceMetadata instMeta = instMan.getInstances().get(0);
        Path instanceDir = Paths.get(mcInstances.toString() + "/" + instMeta.getId());
        Files.createDirectories(instanceDir);

        new ProcessBuilder(
                "java",
                "-Xmx" + instMeta.getMaxMemory(),
                "-Xms" + instMeta.getInitialMemory(),
                "-jar", jarFile.toString(), "nogui")
                .inheritIO().directory(instanceDir.toFile()).start();
    }

}
