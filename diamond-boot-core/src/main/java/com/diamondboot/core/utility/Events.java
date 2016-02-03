/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.core.utility;

import com.google.common.eventbus.Subscribe;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class Events {

    public static class EventBusLogger {

        private EventBusLogger() {
        }

        @Subscribe
        public void logEvent(Object o) {
            System.out.println(o);
        }
    }

    private static final EventBusLogger eventBusLogger = new EventBusLogger();

    public static EventBusLogger getEventBusLogger() {
        return eventBusLogger;
    }

}
