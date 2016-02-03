/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.serverproxy;

import com.diamondboot.core.DiamondBootContext;
import com.diamondboot.core.LocalFileDiamondBootContext;
import com.diamondboot.core.event.DiamondBootEvent;
import com.diamondboot.core.event.MinecraftServerEvent;
import com.diamondboot.core.utility.Events;
import com.diamondboot.serverproxy.instance.LocalMinecraftInstanceManager;
import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.serverproxy.version.MinecraftVersionManager;
import com.diamondboot.serverproxy.version.RemoteJsonMinecraftVersionManager;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class Launcher {

    protected static ExecutorService exec = Executors.newFixedThreadPool(10);
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
        Launcher l = new Launcher(prox, bus);
        l.start();
    }

    private final MinecraftProxy prox;
    private final EventBus bus;

    public Launcher(final MinecraftProxy prox, final EventBus bus) {
        this.prox = prox;
        this.bus = bus;
    }

    public void start() throws IOException {

        bus.register(this);
        bus.register(Events.getEventBusLogger());
        prox.start();
        final Scanner sc = new Scanner(System.in);
        new Thread(() -> {
            while (prox.isRunning()) {
                try {
                    if (System.in.available() > 0 && sc.hasNextLine()) {
                        bus.post(DiamondBootEvent.newEvent(prox.getInstanceMetadata().getId(), sc.nextLine()));
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            exec.shutdown();
        }).start();
    }

    @Subscribe
    public void handleMinecraftEvent(MinecraftServerEvent e) {
        System.out.println("[" + e.getInstanceMetadata().getId() + "] " + e.getContent());
    }
    
    protected EventBus getBus() {
        return bus;
    }
}
