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
package com.diamondboot.modules.status;

import com.diamondboot.modules.core.DiamondBootConfig;
import com.diamondboot.modules.minecraftserver.instances.MinecraftInstanceManager;
import com.diamondboot.modules.minecraftserver.instances.MinecraftInstanceMetadata;
import com.diamondboot.modules.status.DiamondBootStatus.InstanceStatus;
import com.google.inject.name.Named;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class StatusManagerImpl implements StatusManager {

    private final MinecraftInstanceManager instMan;
    private final DiamondBootConfig config;
    private final String version;
    
    @Inject
    public StatusManagerImpl(MinecraftInstanceManager instMan,
            DiamondBootConfig config,
            @Named("diamondBootVersion") String version) {
        this.instMan = instMan;
        this.config = config;
        this.version = version;
    }

    @Override
    public DiamondBootStatus getStatus() throws IOException {
        DiamondBootStatus status = new DiamondBootStatus();

        Map<String,InstanceStatus> instStats = status.getInstances();
        
        for (MinecraftInstanceMetadata meta: instMan.getInstances()) {
            InstanceStatus iStat = new InstanceStatus();
            iStat.setState(meta.isRunning() ? "Running" : "Stopped");
            iStat.setVersion(meta.getVersionMetadata().getId());
            instStats.put(meta.getId(), iStat);
            // TODO call OpCommandInterface
        }
        
        status.setHostname(config.webServer.hostname);
        status.setPort(config.webServer.port);
        status.setVersion(version);
        
        return status;
    }

}
