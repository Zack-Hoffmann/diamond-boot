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

import com.diamondboot.core.DiamondBootContext;
import com.diamondboot.modules.minecraftserver.commands.CommandInterfaceManager;
import com.diamondboot.modules.minecraftserver.commands.OpCommandInterface;
import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.core.metadata.MinecraftInstanceMetadata;
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
    private final CommandInterfaceManager ciMan;
    private final DiamondBootContext context;
    private final String version;

    @Inject
    public StatusManagerImpl(MinecraftInstanceManager instMan,
            CommandInterfaceManager ciMan,
            DiamondBootContext context,
            @Named("diamondBootVersion") String version) {
        this.instMan = instMan;
        this.ciMan = ciMan;
        this.context = context;
        this.version = version;
    }

    @Override
    public DiamondBootStatus getStatus() throws IOException, InterruptedException {
        DiamondBootStatus status = new DiamondBootStatus();

        Map<String, InstanceStatus> instStats = status.getInstances();

        for (MinecraftInstanceMetadata meta : instMan.getInstances()) {
            OpCommandInterface ci = ciMan.getOpCommandInterface(meta.getId());
            InstanceStatus iStat = new InstanceStatus();
            iStat.setState(meta.isRunning() ? "Running" : "Stopped");
            iStat.setVersion(meta.getVersionId());
            if (meta.isRunning()) {
                iStat.setDayTime(ci.time("query", "daytime"));
                iStat.setUpTime(ci.time("query", "gametime"));
                iStat.setPlayersOnline(ci.list().size());
                iStat.setMaxPlayersOnline(ci.maxPlayers());
                iStat.setPlayersBanned(ci.banlist("players").size());
            }
            instStats.put(meta.getId(), iStat);
        }

        status.setHostname(context.getWebServerHostname());
        status.setPort(context.getWebServerPort());
        status.setVersion(version);

        return status;
    }

}
