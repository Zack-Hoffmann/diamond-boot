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
import com.diamondboot.serverproxy.instance.LocalMinecraftInstanceManager;
import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.serverproxy.version.MinecraftVersionManager;
import com.diamondboot.serverproxy.version.RemoteJsonMinecraftVersionManager;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class Launcher {

    public static void main(String[] args) throws IOException {
        String appDir = args.length > 0 ? args[0]
                : (System.getProperty("user.home") + "/diamond-boot");
        String instId = args.length > 1 ? args[1]
                : "default-inst";
        EventBus bus = new EventBus();
        DiamondBootContext ctx = new LocalFileDiamondBootContext(appDir);
        MinecraftVersionManager versMan = new RemoteJsonMinecraftVersionManager("https://s3.amazonaws.com/Minecraft.Download/versions/", "versions.json", ctx);
        MinecraftInstanceManager instMan = new LocalMinecraftInstanceManager(ctx, versMan);
        MinecraftProxy prox = new ProcessBuilderMinecraftProxyFactory(instMan, versMan, bus).get(instId);
        Launcher l = new Launcher(prox, bus);
        bus.register(l);
        prox.start();
        l.startInput();
    }

    private final MinecraftProxy prox;
    private final EventBus bus;

    public Launcher(final MinecraftProxy prox, final EventBus bus) {
        this.prox = prox;
        this.bus = bus;
    }

    public void startInput() {
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
        }).start();
    }

    @Subscribe
    public void handleMinecraftEvent(MinecraftServerEvent e) {
        System.out.println("[" + e.getInstanceMetadata().getId() + "] " + e.getContent());
    }
}
