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
package com.diamondboot.modules.core;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
class DiamondBootConfig {

    public static DiamondBootConfig getDefaultConfig() {
        DiamondBootConfig def = new DiamondBootConfig();
        def.versions.dir = "mc-versions";
        def.instances.dir = "mc-instances";
        def.instances.defaults.initialMemory = "1024M";
        def.instances.defaults.maximumMemory = "1024M";
        def.instances.defaults.version = "RECENT";
        def.instances.startOnLaunch = Arrays.asList("default-inst");
        def.webServer.hostname = "localhost";
        def.webServer.port = 8080;
        return def;
    }

    public static class Versions {

        public String dir;
    }

    public static class Instances {

        public static class Defaults {

            public String initialMemory;
            public String maximumMemory;
            public String version;
        }

        public Defaults defaults = new Defaults();
        public String dir;
        public List<String> startOnLaunch = ImmutableList.of();
    }
    
    public static class WebServer {
        public String hostname;
        public int port;
    }

    public Versions versions = new Versions();
    public Instances instances = new Instances();
    public WebServer webServer = new WebServer();
}
