/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.serverproxy;

import com.diamondboot.core.metadata.MinecraftInstanceMetadata;
import com.diamondboot.core.metadata.MinecraftVersionMetadata;
import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.serverproxy.version.MinecraftVersionManager;
import com.google.common.eventbus.EventBus;
import java.io.IOException;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ProcessBuilderMinecraftProxyFactory implements MinecraftProxyFactory {
    
    private final MinecraftInstanceManager instMan;
    private final MinecraftVersionManager versMan;
    private final EventBus bus;
    
    @Inject
    public ProcessBuilderMinecraftProxyFactory(MinecraftInstanceManager instMan,
            MinecraftVersionManager versMan,
            EventBus bus) {
        this.instMan = instMan;
        this.versMan = versMan;
        this.bus = bus;
    }

    @Override
    public MinecraftProxy create(String instance) throws IOException {
        MinecraftInstanceMetadata inst = instMan.getInstance(instance);
        MinecraftVersionMetadata vers = versMan.getInstalledVersion(inst.getVersionId());
        return new ProcessBuilderMinecraftProxy(vers, inst, bus);
    }
    
}
