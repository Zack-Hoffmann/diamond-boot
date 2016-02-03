/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script.command.impl;

import com.diamondboot.script.command.AbstractMinecraftCommand;
import com.google.common.eventbus.EventBus;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class StopImpl extends AbstractMinecraftCommand<Void> {

    public StopImpl(String instance, EventBus bus) {
        super(instance, bus);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public Void apply(String... t) {
        invoke();
        return null;
    }

}
