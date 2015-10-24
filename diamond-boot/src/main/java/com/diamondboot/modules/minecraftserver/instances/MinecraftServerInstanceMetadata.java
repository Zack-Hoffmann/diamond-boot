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

import com.diamondboot.modules.minecraftserver.versions.MinecraftVersionMetadata;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.file.Path;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
@JsonIgnoreProperties({"dir"})
public class MinecraftServerInstanceMetadata {

    private String id;
    private String maxMemory;
    private String initialMemory;
    private MinecraftVersionMetadata versionMetadata;
    private Path dir;
    private boolean running;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(String maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getInitialMemory() {
        return initialMemory;
    }

    public void setInitialMemory(String initialMemory) {
        this.initialMemory = initialMemory;
    }

    public MinecraftVersionMetadata getVersionMetadata() {
        return versionMetadata;
    }

    public void setVersionMetadata(MinecraftVersionMetadata versionMetadata) {
        this.versionMetadata = versionMetadata;
    }

    public Path getDir() {
        return dir;
    }

    @JsonProperty("path")
    public String getDirString() {
        return dir.toString();
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

}
