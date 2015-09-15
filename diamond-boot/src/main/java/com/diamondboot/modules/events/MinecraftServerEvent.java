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
package com.diamondboot.modules.events;

import com.diamondboot.modules.minecraftserver.instances.MinecraftServerInstanceMetadata;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class MinecraftServerEvent {
    
    public static MinecraftServerEvent newInstance(MinecraftServerInstanceMetadata meta, String content) {
        MinecraftServerEvent e = new MinecraftServerEvent();
        e.setContent(content);
        e.setInstanceMetadata(meta);
        return e;
    }

    private MinecraftServerInstanceMetadata instanceMetadata;
    private String content;

    public MinecraftServerInstanceMetadata getInstanceMetadata() {
        return instanceMetadata;
    }

    public void setInstanceMetadata(MinecraftServerInstanceMetadata instanceMetadata) {
        this.instanceMetadata = instanceMetadata;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}