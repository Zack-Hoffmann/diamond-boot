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
import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionMetadata;
import com.google.gson.Gson;
import com.google.inject.Provider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class DiamondBootContextProvider implements Provider<DiamondBootContext> {

    private final String appDir;
    private final Gson gson = new Gson();

    protected DiamondBootContextProvider(String appDir) {
        this.appDir = appDir;
    }

    @Override
    public DiamondBootContext get() {
        final Path appDirPath = Paths.get(appDir);
        final Path appPropsPath = Paths.get(appDir + "/app.json");

        try {
            if (Files.notExists(appDirPath)) {
                Files.createDirectories(appDirPath);
            }

            if (Files.notExists(appPropsPath)) {
                Files.createFile(appPropsPath);
                try (FileWriter w = new FileWriter(appPropsPath.toFile())) {
                    w.write(gson.toJson(DiamondBootConfig.getDefaultConfig()));
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Diamond Boot cannot be started in the provided directory \"" + appDir + "\".", e);
        }

        DiamondBootConfig conf = new DiamondBootConfig();
        try (FileReader r = new FileReader(appPropsPath.toFile())) {
            conf = gson.fromJson(r, DiamondBootConfig.class);
        } catch (IOException ex) {
            Logger.getLogger(DiamondBootContextProvider.class.getName()).log(
                    Level.SEVERE,
                    "The properties file at \"" + appPropsPath + "\" could not be used.  "
                    + "Ensure that the file is valid and readable, or delete the file and restart the application to restore default values.",
                    ex);
        }

        final Path mcVersDir = Paths.get(appDir + "/" + conf.versions.dir);
        final Path mcInstDir = Paths.get(appDir + "/" + conf.instances.dir);
        final String initialMemory = conf.instances.defaults.initialMemory;
        final String maxMemory = conf.instances.defaults.maximumMemory;
        final List<String> startOnLaunch = conf.instances.startOnLaunch;

        // TODO this is a mess.  need to find a better way to build this
        return new DiamondBootContext() {
            @Override
            public Path getAppDirectory() {
                return appDirPath;
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
            public MinecraftServerInstanceMetadata newDefaultInstanceMetadata(String id, MinecraftVersionMetadata servMeta) {
                MinecraftServerInstanceMetadata meta = new MinecraftServerInstanceMetadata();
                meta.setId(id);
                meta.setInitialMemory(initialMemory);
                meta.setMaxMemory(maxMemory);
                meta.setVersionMetadata(servMeta);
                meta.setDir(Paths.get(mcInstDir.toString() + "/" + id));

                return meta;
            }

            @Override
            public List<String> getStartOnLaunchInstances() {
                return startOnLaunch;
            }

        };
    }

}
