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
package com.diamondboot.modules.core;

import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstanceMetadata;
import com.diamondboot.modules.minecraftserver.versions.MinecraftServerVersionManager;
import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionMetadata;
import com.google.gson.Gson;
import com.google.inject.name.Named;
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

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class LocalFileDiamondBootContext implements DiamondBootContext {

    private final Path appDir;
    private final Path appConf;
    private MinecraftServerVersionManager verMan;
    private final Gson gson = new Gson();

    private final Path mcVersDir;
    private final Path mcInstDir;
    private final String initialMemory;
    private final String maxMemory;
    private final List<String> startOnLaunch;

    @Inject
    public LocalFileDiamondBootContext(@Named("appDir") String appDir,
            MinecraftServerVersionManager verMan) {
        this.appDir = Paths.get(appDir);
        this.verMan = verMan;

        this.appConf = Paths.get(appDir + "/app.json");

        try {
            if (Files.notExists(this.appDir)) {
                Files.createDirectories(this.appDir);
            }

            if (Files.notExists(appConf)) {
                Files.createFile(appConf);
                try (FileWriter w = new FileWriter(appConf.toFile())) {
                    w.write(gson.toJson(DiamondBootConfig.getDefaultConfig()));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Diamond Boot cannot be started in the provided directory \"" + appDir + "\".", e);
        }

        DiamondBootConfig conf = new DiamondBootConfig();
        try (FileReader r = new FileReader(appConf.toFile())) {
            conf = gson.fromJson(r, DiamondBootConfig.class);
        } catch (IOException ex) {
            Logger.getLogger(LocalFileDiamondBootContext.class.getName()).log(
                    Level.SEVERE,
                    "The properties file at \"" + appConf + "\" could not be used.  "
                    + "Ensure that the file is valid and readable, or delete the file and restart the application to restore default values.",
                    ex);
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
    public MinecraftServerInstanceMetadata newDefaultInstanceMetadata(String id) throws IOException {
        MinecraftServerInstanceMetadata meta = new MinecraftServerInstanceMetadata();
        meta.setId(id);
        meta.setInitialMemory(initialMemory);
        meta.setMaxMemory(maxMemory);
        meta.setVersionMetadata(verMan.getLatestVersion());
        meta.setDir(Paths.get(mcInstDir.toString() + "/" + id));

        return meta;
    }

}
