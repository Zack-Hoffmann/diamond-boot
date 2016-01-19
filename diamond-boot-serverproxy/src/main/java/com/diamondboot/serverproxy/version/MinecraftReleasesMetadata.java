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
package com.diamondboot.serverproxy.version;

import com.diamondboot.core.metadata.MinecraftVersionMetadata;
import java.util.List;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class MinecraftReleasesMetadata {

    public static class Latest {
        public String snapshot;
        public String release;
    }

    private Latest latest;
    private List<MinecraftVersionMetadata> versions;

    public Latest getLatest() {
        return latest;
    }

    public void setLatest(Latest latest) {
        this.latest = latest;
    }

    public List<MinecraftVersionMetadata> getVersions() {
        return versions;
    }

    public void setVersions(List<MinecraftVersionMetadata> versions) {
        this.versions = versions;
    }

    public String getLatestSnapshot() {
        return latest.snapshot;
    }

    public void setLatestSnapshot(String snapshot) {
        this.latest.snapshot = snapshot;
    }

    public String getLatestRelease() {
        return latest.release;
    }

    public void setLatestRelease(String release) {
        this.latest.release = release;
    }

}
