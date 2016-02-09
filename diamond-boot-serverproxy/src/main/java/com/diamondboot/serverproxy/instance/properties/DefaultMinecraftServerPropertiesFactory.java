/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.serverproxy.instance.properties;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class DefaultMinecraftServerPropertiesFactory {
    
    List<MinecraftServerProperty> properties = Arrays.asList(new MinecraftServerProperty[]{
        new MinecraftServerProperty("spawn-protection", "Spawn Protection", MinecraftServerProperty.Type.NUMERIC),
        new MinecraftServerProperty("allow-flight", "Allow Flight Mods", MinecraftServerProperty.Type.BOOLEAN),
        new MinecraftServerProperty("allow-nether", "Allow Nether Portals", MinecraftServerProperty.Type.BOOLEAN),
        new MinecraftServerProperty("announce-player-achievements", "Announce Player Achievements", MinecraftServerProperty.Type.BOOLEAN)
            // TODO finish property list
    });
    
}
