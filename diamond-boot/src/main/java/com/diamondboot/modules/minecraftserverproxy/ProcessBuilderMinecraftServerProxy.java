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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ProcessBuilderMinecraftServerProxy implements MinecraftServerProxy {
/*
    private static final List<String> START_COMMAND = Arrays.asList(new String[]{
        "java", "-Xmx1024M", "-Xms1024M", "-jar", "minecraft_server.1.8.8.jar", "nogui"
    });
*/
    private final Path baseDir;
    private final Path mcVersions;
    private final Path mcInstances;
    private final String mcServerMojangUrl;

    @Inject
    public ProcessBuilderMinecraftServerProxy(
            @Named("baseDir") String baseDir,
            @Named("mcVersionsDirectory") String mcVersionsDirectory,
            @Named("mcInstancesDirectory") String mcInstancesDirectory,
            @Named("mcServerMojangUrl") String mcServerMojangUrl) {
        this.baseDir = Paths.get(baseDir);
        this.mcVersions = Paths.get(baseDir + "/" + mcVersionsDirectory);
        this.mcInstances = Paths.get(baseDir + "/" + mcInstancesDirectory);
        this.mcServerMojangUrl = mcServerMojangUrl;

    }

    @Override
    public void start() throws IOException {
        // Minecraft version JSONs
        // http://wiki.vg/Game_Files
        
        Files.createDirectories(baseDir);
        Files.createDirectories(mcVersions);
        Files.createDirectories(mcInstances);

        Path jarFile = Paths.get(mcVersions.toString() + "/minecraft_server.1.8.8.jar");

        if (Files.notExists(jarFile)) {
            HttpURLConnection con = (HttpURLConnection) new URL(mcServerMojangUrl).openConnection();

            try (
                    ReadableByteChannel rc = Channels.newChannel(con.getInputStream());
                    FileChannel fc = FileChannel.open(jarFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                fc.transferFrom(rc, 0, 10000000);
            }
        }
        // TODO run process in mc-instances using jar in mc-versions
        // TODO 
        // new ProcessBuilder(START_COMMAND).inheritIO().start();
    }

}
