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
package com.diamondboot.modules.minecraftserver.proxy;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class MinecraftProxyModule extends AbstractModule {

    @Override
    protected void configure() {
        // TODO move to config file
        bind(String.class).annotatedWith(Names.named("mcVersionsBaseUrl")).toInstance("https://s3.amazonaws.com/Minecraft.Download/versions/");
        bind(String.class).annotatedWith(Names.named("mcVersionsJsonUrl")).toInstance("versions.json");
  
        install(new FactoryModuleBuilder()
                .implement(MinecraftProxy.class, ProcessBuilderMinecraftProxy.class)
                .build(MinecraftProxyFactory.class));
    }

}
