/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.shell;

import com.diamondboot.core.DiamondBootContext;
import com.diamondboot.core.LocalFileDiamondBootContext;
import com.diamondboot.core.event.MinecraftServerEvent;
import com.diamondboot.core.metadata.MinecraftInstanceMetadata;
import com.diamondboot.serverproxy.MinecraftProxy;
import com.diamondboot.serverproxy.MinecraftProxyFactory;
import com.diamondboot.serverproxy.ProcessBuilderMinecraftProxyFactory;
import com.diamondboot.serverproxy.instance.LocalMinecraftInstanceManager;
import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.serverproxy.version.MinecraftVersionManager;
import com.diamondboot.serverproxy.version.RemoteJsonMinecraftVersionManager;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.io.Console;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zack Hoffmann
 */
public class Launcher {

    private static final EventBus BUS = new AsyncEventBus(Executors.newFixedThreadPool(10));
    private static final DiamondBootContext ctx = new LocalFileDiamondBootContext(System.getProperty("user.home") + "/diamond-boot");
    private static final MinecraftVersionManager vm = new RemoteJsonMinecraftVersionManager("https://s3.amazonaws.com/Minecraft.Download/versions/", "versions.json", ctx);
    private static final MinecraftInstanceManager IM = new LocalMinecraftInstanceManager(ctx, vm);
    private static final MinecraftProxyFactory FACTORY = new ProcessBuilderMinecraftProxyFactory(IM, vm, BUS);

    static {

    }

    public static void main(String... args) {
        //final Console cons = System.console();
        final Scanner sc = new Scanner(System.in);

        BUS.register((Consumer<MinecraftServerEvent>) (MinecraftServerEvent t) -> System.out.println(t.getContent()));

        new Thread(() -> {
            String in;
            do {
                System.out.print("%> ");
                in = sc.nextLine();
                try {
                    execute(in.split("\\s+"));
                } catch (InvalidCommandException | CommandFailedException ex) {
                    System.out.print(ex.getMessage());
                }
            } while (!in.equalsIgnoreCase("exit"));
        }).start();

    }

    private static void execute(String... args) throws InvalidCommandException, CommandFailedException {
        switch (args[0]) {
            case "create":
                String createInstanceName = getArg(1, args);
                try {
                    IM.newInstance(createInstanceName);
                    vm.installVersion("RECENT");
                } catch (IOException ex) {
                    throw new CommandFailedException("Could not create command.", ex);
                }
                break;
            case "start":
                String startInstanceName = getArg(1, args);
                try {
                    MinecraftProxy px = FACTORY.get(startInstanceName);
                    px.start();
                } catch (IOException ex) {
                    throw new CommandFailedException("Could not start Minecraft proxy.", ex);
                }
                break;
            case "stop":
                String stopInstanceName = getArg(1, args);
                try {
                    MinecraftProxy px = FACTORY.get(stopInstanceName);
                    px.stop();
                } catch (IOException ex) {
                    throw new CommandFailedException("Could not start Minecraft proxy.", ex);
                }
                break;
        }
    }

    private static String getArg(int pos, String... args) throws InvalidCommandException {
        try {
            return args[pos];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new InvalidCommandException("Not enough arguments.", ex);
        }
    }
}
