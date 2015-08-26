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
package com.diamondboot.modules.minecraftserverproxy;

import com.diamondboot.modules.minecraftserverproxy.versions.RemoteJsonMinecraftServerVersionManager;
import com.diamondboot.modules.minecraftserverproxy.versions.MinecraftServerVersionManager;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class MinecraftServerProxyModule extends AbstractModule {

    private final String baseDir;

    public MinecraftServerProxyModule(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("baseDir")).toInstance(baseDir);
        bind(String.class).annotatedWith(Names.named("mcVersionsDirectory")).toInstance("mc-versions");
        bind(String.class).annotatedWith(Names.named("mcInstancesDirectory")).toInstance("mc-instances");
        bind(String.class).annotatedWith(Names.named("mcVersionsBaseUrl")).toInstance("https://s3.amazonaws.com/Minecraft.Download/versions/");
        bind(String.class).annotatedWith(Names.named("mcVersionsJsonUrl")).toInstance("versions.json");

        bind(MinecraftServerVersionManager.class).to(RemoteJsonMinecraftServerVersionManager.class).in(Scopes.SINGLETON);
        bind(MinecraftServerProxy.class).to(ProcessBuilderMinecraftServerProxy.class);
    }

}
