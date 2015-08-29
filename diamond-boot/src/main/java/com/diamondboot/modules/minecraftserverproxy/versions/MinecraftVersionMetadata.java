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

import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class MinecraftVersionMetadata {

    private String id;
    private Date time;
    private Date releaseTime;
    private String type;
    private Path jarFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Optional<Path> getJarFile(){
        return Optional.ofNullable(jarFile);
    }
    
    public void setJarFile(Path jarFile) {
        this.jarFile = jarFile;
    }
}
