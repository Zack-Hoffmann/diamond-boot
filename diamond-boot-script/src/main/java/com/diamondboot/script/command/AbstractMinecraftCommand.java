/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script.command;

import com.diamondboot.core.event.DiamondBootEvent;
import com.diamondboot.core.event.MinecraftServerEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 * @param <R>
 */
public abstract class AbstractMinecraftCommand<R> implements MinecraftCommand<R> {
    
    private final String instance;
    private final EventBus bus;
    private final BlockingQueue<String> responses = new LinkedBlockingQueue<>();
    
    public AbstractMinecraftCommand(String instance, EventBus bus) {
        this.instance = instance;
        this.bus = bus;
    }
        
    public void invoke(String... args) {
        // TODO Log
        System.out.println("Registering for responses.");
        bus.register(this);
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        for (String a : args) {
            sb.append(" ").append(a);
        }
        System.out.println("Posting invoke event.");
        bus.post(DiamondBootEvent.newEvent(instance, sb.toString()));
    }

    @Subscribe
    public void queueResponses(MinecraftServerEvent e) {
        // TODO Log
        System.out.println("Response received.");
        if (e.getInstanceMetadata().getId().equals(instance)) {
            responses.add(e.getContent());
        }
    }
    
    protected String takeResponse() {
        // TODO Log
        System.out.println("Awaiting response.");
        try {
            return responses.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractMinecraftCommand.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
}
