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
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ProcessBuilderMinecraftProxyFactory implements MinecraftProxyFactory {

    private final MinecraftInstanceManager instMan;
    private final MinecraftVersionManager versMan;
    private final EventBus bus;
    private final Map<String, MinecraftProxy> proxies = Maps.newHashMap();

    @Inject
    public ProcessBuilderMinecraftProxyFactory(MinecraftInstanceManager instMan,
            MinecraftVersionManager versMan,
            EventBus bus) {
        this.instMan = instMan;
        this.versMan = versMan;
        this.bus = bus;
    }

    @Override
    public MinecraftProxy get(String instance) throws IOException {
        return proxies.computeIfAbsent(instance, i -> {
            try {
                MinecraftInstanceMetadata inst = instMan.getInstance(i);
                MinecraftVersionMetadata vers = versMan.getInstalledVersion(inst.getVersionId());
                return new ProcessBuilderMinecraftProxy(vers, inst, bus);
            } catch (IOException e) {
                return null;
            }
        });
    }

}
