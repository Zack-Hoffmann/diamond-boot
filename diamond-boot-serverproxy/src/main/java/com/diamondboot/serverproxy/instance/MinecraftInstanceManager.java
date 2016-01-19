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
package com.diamondboot.serverproxy.instance;

import com.diamondboot.core.metadata.MinecraftInstanceMetadata;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public interface MinecraftInstanceManager {

    List<MinecraftInstanceMetadata> getInstances() throws IOException;
    MinecraftInstanceMetadata getInstance(String id) throws IOException;
    MinecraftInstanceMetadata newInstance(String id) throws IOException;
    void startInstance(String id) throws IOException;
    void stopInstance(String id) throws IOException;
}
