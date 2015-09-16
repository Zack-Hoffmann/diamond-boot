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
package com.diamondboot.modules.minecraftserver.instances;

import com.diamondboot.modules.core.DiamondBootContext;
import com.diamondboot.modules.minecraftserver.proxy.MinecraftServerProxy;
import com.diamondboot.modules.minecraftserver.proxy.MinecraftServerProxyFactory;
import com.diamondboot.modules.minecraftserver.versions.MinecraftServerVersionManager;
import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionMetadata;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class LocalFileMinecraftServerInstanceManager implements MinecraftServerInstanceManager {

    private final DiamondBootContext ctx;
    private final MinecraftServerVersionManager versMan;
    private final MinecraftServerProxyFactory pxf;
    private final Gson gson = new Gson();

    @Inject
    public LocalFileMinecraftServerInstanceManager(DiamondBootContext ctx,
            MinecraftServerVersionManager versMan,
            MinecraftServerProxyFactory pxf) {
        this.versMan = versMan;
        this.ctx = ctx;
        this.pxf = pxf;
    }

    @Override
    public List<MinecraftServerInstanceMetadata> getInstances() throws IOException {
        return getInstanceIds().stream()
                .map(i -> {
                    MinecraftServerInstanceMetadata meta = null;
                    try {
                        MinecraftServerInstanceConfiguration conf = getInstanceConfiguration(i);
                        MinecraftVersionMetadata vers = versMan.getInstalledVersion(conf.getVersionId()).orElse(versMan.getLatestVersion());

                        meta = new MinecraftServerInstanceMetadata();
                        meta.setId(conf.getInstanceId());
                        meta.setInitialMemory(conf.getInitialMemory());
                        meta.setMaxMemory(conf.getMaxMemory());
                        meta.setVersionMetadata(vers);
                        meta.setDir(getInstanceDirectory(conf.getInstanceId()));
                    } catch (IOException ex) {
                        Logger.getLogger(LocalFileMinecraftServerInstanceManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return meta;
                }).collect(Collectors.toList());
    }

    @Override
    public Optional<MinecraftServerInstanceMetadata> getInstance(String id) throws IOException {
        return getInstances().stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    @Override
    public MinecraftServerInstanceMetadata newInstance(String id) throws IOException {
        if (!getInstanceIds().contains(id)) {

            Files.createDirectories(getInstanceDirectory(id));

            MinecraftServerInstanceMetadata meta = ctx.newDefaultInstanceMetadata(id);

            MinecraftServerInstanceConfiguration conf = new MinecraftServerInstanceConfiguration();
            conf.setInstanceId(meta.getId());
            conf.setInitialMemory(meta.getInitialMemory());
            conf.setMaxMemory(meta.getMaxMemory());
            conf.setVersionId(meta.getVersionMetadata().getId());

            writeInstanceConfiguration(conf);

            return meta;
        } else {
            return null;
        }
    }

    private List<String> getInstanceIds() throws IOException {
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

    private Path getInstanceDirectory(String id) {
        return Paths.get(ctx.getMinecraftInstancesDirectory() + "/" + id);
    }

    // TODO should probably cache instance json
    private MinecraftServerInstanceConfiguration getInstanceConfiguration(String id) throws IOException {
        String configPath = getInstanceDirectory(id).toString() + "/instance.json";
        try (FileReader r = new FileReader(configPath)) {
            return gson.fromJson(r, MinecraftServerInstanceConfiguration.class);
        }
    }

    private void writeInstanceConfiguration(MinecraftServerInstanceConfiguration conf) throws IOException {
        Path configPath = Paths.get(getInstanceDirectory(conf.getInstanceId()).toString() + "/instance.json");
        if (Files.notExists(configPath)) {
            Files.createFile(configPath);
        }
        try (FileWriter w = new FileWriter(configPath.toFile())) {
            w.write(gson.toJson(conf));
        }
    }

    @Override
    public void runInstance(String id) throws IOException {
        final MinecraftServerProxy px = pxf.create(id);
        px.start();

    }
}
