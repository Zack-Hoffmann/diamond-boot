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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ProcessBuilderMinecraftServerProxy implements MinecraftServerProxy {

    private static final List<String> START_COMMAND = Arrays.asList(new String[]{
        "java", "-Xmx1024M", "-Xms1024M", "-jar", "minecraft_server.1.8.8.jar", "nogui"
    });

    private final String baseDir;
    
    @Inject
    public ProcessBuilderMinecraftServerProxy(@Named("baseDir") String baseDir) {
        this.baseDir = baseDir;
    }
    
    @Override
    public void start() throws IOException {
        
        // TODO if basedir does not exist, create it
        //  THEN if "mc-versions" does not exist, create it
        //  THEN if "mc-instances" does not exist, create it
        //  THEN download jar to mc-versions (separate module)
        //  THEN run process in mc-instances using jar in mc-versions
        new ProcessBuilder(START_COMMAND).inheritIO().start();
    }

}
