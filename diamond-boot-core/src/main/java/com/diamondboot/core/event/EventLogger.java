package com.diamondboot.core.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class EventLogger {
    
    private final Logger log;
    private final EventBus bus;
    
    @Inject
    public EventLogger(Logger log,
            EventBus bus) {
        this.log = log;
        this.bus = bus;
    }
    
    public void start() {
        bus.register(this);
        log.log(Level.FINE, "Event logger listening.");
    }
    
    public void stop() {
        bus.unregister(this);
        log.log(Level.FINER, "Event logger not longer listening.");
    }
    
    @Subscribe
    public void log(Object e) {
        log.log(Level.FINE, e.toString());
    }
    
}
