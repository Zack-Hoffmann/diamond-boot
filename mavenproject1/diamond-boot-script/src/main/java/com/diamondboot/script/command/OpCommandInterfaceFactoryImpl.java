/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script.command;

import com.google.common.eventbus.EventBus;
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
        return new OpCommandInterfaceImpl(instance, bus);
    }
    
}
