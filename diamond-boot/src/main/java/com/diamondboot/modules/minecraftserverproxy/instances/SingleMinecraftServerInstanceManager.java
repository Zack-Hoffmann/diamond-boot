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

import java.util.Arrays;
import java.util.List;

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

    public SingleMinecraftServerInstanceManager() {
        instance = new MinecraftServerInstanceMetadata();
        instance.setId("instance");
        instance.setInitialMemory("1024M");
        instance.setMaxMemory("1024M");
    }

    @Override
    public List<MinecraftServerInstanceMetadata> getInstances() {
        return Arrays.asList(instance);
    }

    @Override
    public void commitInstance(MinecraftServerInstanceMetadata meta) {
        instance.setVersionMetadata(meta.getVersionMetadata());
    }

}
