/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.serverproxy;

import com.diamondboot.core.DiamondBootContext;
import com.diamondboot.core.LocalFileDiamondBootContext;
import com.diamondboot.serverproxy.instance.LocalMinecraftInstanceManager;
import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.serverproxy.version.MinecraftVersionManager;
import com.diamondboot.serverproxy.version.RemoteJsonMinecraftVersionManager;
import com.google.common.eventbus.EventBus;
import java.io.IOException;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class Launcher {
    
    public static void main(String[] args) throws IOException {
        String appDir = args.length > 0 ? args[0]
                    : (System.getProperty("user.home") + "/diamond-boot");
        EventBus bus = new EventBus();
        DiamondBootContext ctx = new LocalFileDiamondBootContext(appDir);
        MinecraftVersionManager versMan = new RemoteJsonMinecraftVersionManager(null, null, ctx);
        MinecraftInstanceManager instMan = new LocalMinecraftInstanceManager(ctx, versMan);
        MinecraftProxyFactory proxF = new ProcessBuilderMinecraftProxyFactory(instMan, versMan, bus);
        proxF.get("default-inst").start();
    }
}
