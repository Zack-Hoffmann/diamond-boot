/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script;

import com.diamondboot.core.DiamondBootContext;
import com.diamondboot.core.LocalFileDiamondBootContext;
import com.diamondboot.core.event.MinecraftServerEvent;
import com.diamondboot.script.command.AbstractMinecraftCommand;
import com.diamondboot.script.command.CommandInvocationHandler;
import com.diamondboot.script.command.MinecraftCommand;
import com.diamondboot.script.command.MinecraftCommandMap;
import com.diamondboot.script.command.OpCommandInterface;
import com.diamondboot.script.command.OpCommandInterfaceFactory;
import com.diamondboot.script.command.OpCommandInterfaceFactoryImpl;
import com.diamondboot.script.command.impl.ListImpl;
import com.diamondboot.script.command.impl.StopImpl;
import com.diamondboot.serverproxy.MinecraftProxy;
import com.diamondboot.serverproxy.ProcessBuilderMinecraftProxyFactory;
import com.diamondboot.serverproxy.instance.LocalMinecraftInstanceManager;
import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.serverproxy.version.MinecraftVersionManager;
import com.diamondboot.serverproxy.version.RemoteJsonMinecraftVersionManager;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class Launcher extends com.diamondboot.serverproxy.Launcher {

    public static void main(String[] args) throws IOException {

        String appDir = args.length > 0 ? args[0]
                : (System.getProperty("user.home") + "/diamond-boot");
        String instId = args.length > 1 ? args[1]
                : "default-inst";
        EventBus bus = new AsyncEventBus(exec);
        DiamondBootContext ctx = new LocalFileDiamondBootContext(appDir);
        MinecraftVersionManager versMan = new RemoteJsonMinecraftVersionManager("https://s3.amazonaws.com/Minecraft.Download/versions/", "versions.json", ctx);
        MinecraftInstanceManager instMan = new LocalMinecraftInstanceManager(ctx, versMan);
        MinecraftProxy prox = new ProcessBuilderMinecraftProxyFactory(instMan, versMan, bus).get(instId);
        Launcher l = new Launcher(prox, bus, instId);
        l.start();
    }
    
    private final String instId;

    public Launcher(MinecraftProxy prox, EventBus bus, String instId) {
        super(prox, bus);
        this.instId = instId;
    }

    @Subscribe
    public void waitForStartup(MinecraftServerEvent e) {
        if (e.getContent().contains("Done")) {
            final OpCommandInterfaceFactory factory = new OpCommandInterfaceFactoryImpl(getBus());
            try {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                engine.put("commands", factory.get(instId));
                engine.eval("commands.list()");
                engine.eval("commands.stop()");
            } catch (ScriptException ex) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
