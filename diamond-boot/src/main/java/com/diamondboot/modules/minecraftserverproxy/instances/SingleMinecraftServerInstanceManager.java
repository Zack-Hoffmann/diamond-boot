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
package com.diamondboot.modules.minecraftserverproxy.instances;

import com.diamondboot.modules.core.DiamondBootContext;
import com.diamondboot.modules.minecraftserverproxy.versions.MinecraftServerVersionManager;
import com.diamondboot.modules.minecraftserverproxy.versions.MinecraftVersionMetadata;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class SingleMinecraftServerInstanceManager implements MinecraftServerInstanceManager {

    /*
     TODOS
     * Create index of instances in instance folder
     * Create metadata file in each instance folder
     */
    private final MinecraftServerInstanceMetadata instance;
    private final DiamondBootContext ctx;
    private final MinecraftServerVersionManager versMan;

    @Inject
    public SingleMinecraftServerInstanceManager(DiamondBootContext ctx,
            MinecraftServerVersionManager versMan) {
        instance = new MinecraftServerInstanceMetadata();
        instance.setId("instance");
        instance.setInitialMemory("1024M");
        instance.setMaxMemory("1024M");

        this.versMan = versMan;
        this.ctx = ctx;
    }

    @Override
    public List<MinecraftServerInstanceMetadata> getInstances() throws IOException {
        Properties defs = ctx.getAppProperties();
        final MinecraftVersionMetadata vers = versMan.getLatestVersion();
        return getInstancesNames().stream().map(i -> {
            // TODO need to save this instead.  using defaults for now
            MinecraftServerInstanceMetadata meta = new MinecraftServerInstanceMetadata();
            meta.setId(i);
            meta.setInitialMemory(defs.getProperty("instances.default.memory.initial"));
            meta.setMaxMemory(defs.getProperty("instances.default.memory.max"));
            meta.setVersionMetadata(vers);
            meta.setDir(Paths.get(ctx.getMinecraftInstancesDirectory().toString() + "/" + i));

            return meta;
        }).collect(Collectors.toList());
    }

    @Override
    public void commitInstance(MinecraftServerInstanceMetadata meta) {
        instance.setVersionMetadata(meta.getVersionMetadata());
    }

    @Override
    public Optional<MinecraftServerInstanceMetadata> getInstance(String id) throws IOException {
        return getInstances().stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    @Override
    public MinecraftServerInstanceMetadata newInstance(String id) throws IOException {
        if (!getInstancesNames().contains(id)) {
            Properties defs = ctx.getAppProperties();

            Path instanceDir = Paths.get(ctx.getMinecraftInstancesDirectory().toString() + "/" + id);
            Files.createDirectories(instanceDir);

            MinecraftServerInstanceMetadata meta = new MinecraftServerInstanceMetadata();
            meta.setId(id);
            meta.setInitialMemory(defs.getProperty("instances.default.memory.initial"));
            meta.setMaxMemory(defs.getProperty("instances.default.memory.max"));
            meta.setVersionMetadata(versMan.getLatestVersion());
            meta.setDir(Paths.get(ctx.getMinecraftInstancesDirectory().toString() + "/" + id));
            
            return meta;
        } else {
            return null;
        }
    }

    private List<String> getInstancesNames() throws IOException {
        ImmutableList.Builder<String> lb = ImmutableList.builder();

        Path dir = ctx.getMinecraftInstancesDirectory();
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            ds.forEach(s -> lb.add(s.getFileName().toString()));
        }

        return lb.build();
    }
}
