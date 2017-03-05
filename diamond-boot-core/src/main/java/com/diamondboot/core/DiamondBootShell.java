/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.core;

import com.diamondboot.core.event.DiamondBootEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Logger;

/**
 *
 * @author Zack Hoffmann
 */
public class DiamondBootShell {
    
    private final Logger log;
    private final EventBus bus;
    
    public DiamondBootShell(Logger log,
            EventBus bus) {
        this.log = log;
        this.bus = bus;
    }
    
    public void start() {
        bus.register(this);
    }
    
    public void stop() {
        bus.unregister(this);
    }
    
    @Subscribe
    public void processShellCommand(DiamondBootEvent ev) {
        if (ev.getTargetInstance().equals(DiamondBootEvent.SHELL)) {
            // TODO SHELL EVENT!
        }
    }
}
