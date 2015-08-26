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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class RemoteJsonMinecraftServerVersionManager implements MinecraftServerVersionManager {

    private final String jsonUrlStr;

    @Inject
    public RemoteJsonMinecraftServerVersionManager(
            @Named("mcVersionsBaseUrl") String baseUrl,
            @Named("mcVersionsJsonUrl") String jsonUrl
    ) {
        this.jsonUrlStr = baseUrl + jsonUrl;
    }

    @Override
    public MinecraftReleasesMetadata getReleasesMetadata() throws IOException {
        URL jsonUrl = new URL(jsonUrlStr);

        String json = "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(jsonUrl.openStream()))) {
            json = br.lines().collect(Collectors.joining());
        }

        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create().fromJson(json, MinecraftReleasesMetadata.class);
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

}
