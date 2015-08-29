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

import com.google.inject.Provider;
import java.io.FileInputStream;
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

    protected DiamondBootContextProvider(String appDir) {
        this.appDir = appDir;
    }

    @Override
    public DiamondBootContext get() {
        final Path appDirPath = Paths.get(appDir);
        final Path appPropsPath = Paths.get(appDir + "/app.properties");

        try {
            if (Files.notExists(appDirPath)) {
                Files.createDirectories(appDirPath);
            }

            if (Files.notExists(appPropsPath)) {
                Files.createFile(appPropsPath);
                writeDefaultProperties(appPropsPath);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Diamond Boot cannot be started in the provided directory \"" + appDir + "\".", e);
        }

        final Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appPropsPath.toString()));
        } catch (IOException ex) {
            Logger.getLogger(DiamondBootContextProvider.class.getName()).log(
                    Level.SEVERE,
                    "The properties file at \"" + appPropsPath + "\" could not be used.  "
                    + "Ensure that the file is valid and readable, or delete the file and restart the application to restore default values.",
                    ex);
        }

        final Path mcVersDir = Paths.get(appDir + "/" + appProps.getProperty("versions.dir"));
        final Path mcInstDir = Paths.get(appDir + "/" + appProps.getProperty("instances.dir"));

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
            public Properties getAppProperties() {
                return appProps;
            }

        };
    }

    private static void writeDefaultProperties(Path propFilePath) throws IOException {
        final List<String> defaultLines = Arrays.asList(new String[]{
            "# Diamond Boot Application Properties",
            "# Default properties generated " + new Date(),
            "",
            "# Relative path of Minecraft Server JAR files",
            "versions.dir=mc-versions",
            "",
            "# Relative path of Minecraft Server instances",
            "instances.dir=mc-instances",
            "",
            "# Default memory allocations when creating a new instance",
            "instances.default.memory.initial=1024M",
            "instances.default.memory.max=1024M",
            "",
            "# Default minecraft version ID when creating a new instance",
            "# \"RECENT\" will use the most recent non-snapshot release",
            "instances.default.version=RECENT",
            "",
            "# List of instances to automatically create and/or start when Diamond Boot launches",
            "instances.startOnLaunch=default-inst"
        });

        StringBuilder sb = new StringBuilder();
        defaultLines.stream().forEach(l -> sb.append(l).append("\n"));

        Files.write(propFilePath, sb.toString().getBytes(), StandardOpenOption.WRITE);
    }

}
