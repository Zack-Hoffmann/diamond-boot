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
package com.diamondboot.core;

import com.diamondboot.core.metadata.MinecraftInstanceMetadata;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class LocalFileDiamondBootContext implements DiamondBootContext {

    private final Logger log;
    
    private final Path appDir;
    private final Path appConf;
    private final Gson gson = new Gson();
    private final DiamondBootConfig conf;

    private final Path mcVersDir;
    private final Path mcInstDir;
    private final String initialMemory;
    private final String maxMemory;
    private final List<String> startOnLaunch;

    @Inject
    public LocalFileDiamondBootContext(final Logger log,
            @Named("appDir") String appDir) {
        this.log = log;
        log.log(Level.CONFIG, "Initializing Diamond Boot Context.");
        this.appDir = Paths.get(appDir);

        this.appConf = Paths.get(appDir + "/app.json");
        
        log.log(Level.CONFIG, "Configuration file is {0}.", this.appConf);

        try {
            if (Files.notExists(this.appDir)) {
                log.log(Level.CONFIG, "{0} does not exist and will be created.", this.appDir);
                Files.createDirectories(this.appDir);
            }

            if (Files.notExists(appConf)) {
                log.log(Level.CONFIG, "{0} does not exist and will be created.", this.appConf);
                Files.createFile(appConf);
                log.log(Level.CONFIG, "Initializing {0} with default values.", this.appConf);
                try (FileWriter w = new FileWriter(appConf.toFile())) {
                    w.write(gson.toJson(DiamondBootConfig.getDefaultConfig()));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Diamond Boot cannot be started in the provided directory \"" + appDir + "\".", e);
        }

        try (FileReader r = new FileReader(appConf.toFile())) {
            conf = gson.fromJson(r, DiamondBootConfig.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "The properties file at \"" + appConf + "\" could not be used.  "
                    + "Ensure that the file is valid and readable, or delete the file and restart the application to restore default values.",
                    e);
        }

        mcVersDir = Paths.get(appDir + "/" + conf.versions.dir);
        mcInstDir = Paths.get(appDir + "/" + conf.instances.dir);
        initialMemory = conf.instances.defaults.initialMemory;
        maxMemory = conf.instances.defaults.maximumMemory;
        startOnLaunch = conf.instances.startOnLaunch;
    }

    @Override
    public Path getAppDirectory() {
        return appDir;
    }

    @Override
    public Path getMinecraftVersionsDirectory() {
        return mcVersDir;
    }

    @Override
    public Path getMinecraftInstancesDirectory() {
        return mcInstDir;
    }

    @Override
    public List<String> getStartOnLaunchInstances() {
        return startOnLaunch;
    }

    @Override
    public MinecraftInstanceMetadata newDefaultInstanceMetadata(String id) throws IOException {
        MinecraftInstanceMetadata meta = new MinecraftInstanceMetadata();
        meta.setId(id);
        meta.setInitialMemory(initialMemory);
        meta.setMaxMemory(maxMemory);
        meta.setVersionId(conf.instances.defaults.version);
        meta.setDir(Paths.get(mcInstDir.toString() + "/" + id));

        return meta;
    }

    @Override
    public int getWebServerPort() {
        return conf.webServer.port;
    }

    @Override
    public String getWebServerHostname() {
        return conf.webServer.hostname;
    }

}
