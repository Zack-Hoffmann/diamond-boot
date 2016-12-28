/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script.command;

import com.diamondboot.script.command.impl.ListImpl;
import com.diamondboot.script.command.impl.StopImpl;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class OpCommandInterfaceFactoryImpl implements OpCommandInterfaceFactory {

    private final EventBus bus;

    @Inject
    public OpCommandInterfaceFactoryImpl(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public OpCommandInterface get(String instance) {
        final Collection<AbstractMinecraftCommand<? extends Object>> COMMANDS = Lists.newArrayList(
                new ListImpl(instance, bus),
                new StopImpl(instance, bus)
        );
        MinecraftCommandMap commandMap = new MinecraftCommandMap();
        commandMap.putAll(COMMANDS.stream().collect(Collectors.toMap(MinecraftCommand::getName, Function.identity())));
        return (OpCommandInterface) Proxy.newProxyInstance(OpCommandInterface.class.getClassLoader(), new Class<?>[]{OpCommandInterface.class}, new CommandInvocationHandler(commandMap));
    }

}
