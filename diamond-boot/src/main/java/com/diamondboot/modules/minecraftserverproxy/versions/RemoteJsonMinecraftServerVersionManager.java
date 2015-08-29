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
package com.diamondboot.modules.minecraftserverproxy.versions;

import com.diamondboot.modules.core.DiamondBootContext;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class RemoteJsonMinecraftServerVersionManager implements MinecraftServerVersionManager {

    private final String jsonUrlStr;
    private final String baseUrl;
    private final DiamondBootContext ctx;

    @Inject
    public RemoteJsonMinecraftServerVersionManager(
            @Named("mcVersionsBaseUrl") String baseUrl,
            @Named("mcVersionsJsonUrl") String jsonUrl,
            DiamondBootContext ctx
    ) {
        this.jsonUrlStr = baseUrl + jsonUrl;
        this.baseUrl = baseUrl;
        this.ctx = ctx;
    }

    @Override
    public MinecraftReleasesMetadata getReleasesMetadata() throws IOException {
        URL jsonUrl = new URL(jsonUrlStr);

        String json;

        // TODO Cache JSON
        try (BufferedReader br = new BufferedReader(new InputStreamReader(jsonUrl.openStream()))) {
            json = br.lines().collect(Collectors.joining());
        }

        MinecraftReleasesMetadata mrmd = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create().fromJson(json, MinecraftReleasesMetadata.class);
        Map<String, Path> installedDat = getInstalledDatMap();
        mrmd.getVersions().stream().forEach(v -> v.setJarFile(installedDat.get(v.getId())));

        return mrmd;
    }

    @Override
    public List<MinecraftVersionMetadata> getAvailableVersions() throws IOException {
        return getReleasesMetadata().getVersions();
    }

    @Override
    public MinecraftVersionMetadata getLatestVersion() throws IOException {
        MinecraftReleasesMetadata meta = getReleasesMetadata();
        return meta.getVersions().stream()
                .filter(v -> v.getId().equals(meta.getLatestRelease()))
                .findFirst().get();
    }

    @Override
    public List<MinecraftVersionMetadata> getInstalledVersions() throws IOException {
        Set<String> vers = getInstalledDatMap().keySet();
        return getAvailableVersions().stream()
                .filter(v -> vers.contains(v.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public MinecraftVersionMetadata installVersion(String version) throws IOException {
        if (getInstalledDatMap().get(version) == null) {

            String ver = getLatestVersion().getId();

            String fileName = "minecraft_server." + ver + ".jar";
            Path jarFile = Paths.get(ctx.getMinecraftVersionsDirectory().toString() + "/" + fileName);
            String fullDownloadUrl = baseUrl + ver + "/" + fileName;

            if (Files.notExists(jarFile)) {
                HttpURLConnection con = (HttpURLConnection) new URL(fullDownloadUrl).openConnection();

                try (
                        ReadableByteChannel rc = Channels.newChannel(con.getInputStream());
                        FileChannel fc = FileChannel.open(jarFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    fc.transferFrom(rc, 0, 10000000);
                }
            }

            Files.write(getInstalledDat(), (ver + " " + jarFile + "\n").getBytes(), StandardOpenOption.APPEND);
        }

        return getInstalledVersion(version).get();
    }

    @Override
    public Optional<MinecraftVersionMetadata> getInstalledVersion(String version) throws IOException {
        return getInstalledVersions().stream().filter(v -> v.getId().equals(version)).findFirst();
    }

    private Path getInstalledDat() throws IOException {
        Path dir = ctx.getMinecraftInstancesDirectory();
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
        Path installedList = Paths.get(dir.toString() + "/installed.dat");

        if (Files.notExists(installedList)) {
            Files.createFile(installedList);
        }
        return installedList;
    }

    private Map<String, Path> getInstalledDatMap() throws IOException {
        Path dir = ctx.getMinecraftInstancesDirectory();
        return Files.lines(getInstalledDat())
                .filter(s -> s.trim().length() > 0)
                .map(s -> s.split(" "))
                .collect(Collectors.toMap(
                                (sa -> sa[0]),
                                (sa -> Paths.get(dir.toString() + "/" + sa[1]))));
    }

}
