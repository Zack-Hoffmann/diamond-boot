/*
 * Copyright 2015 Zack Hoffmann <zachary.hoffmann@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diamondboot.modules.web;

import com.diamondboot.core.DiamondBootContext;
import com.google.inject.servlet.GuiceFilter;
import java.util.EnumSet;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class JettyDiamondBootWebServer implements DiamondBootWebServer {

    private final Server serv;

    @Inject
    public JettyDiamondBootWebServer(DiamondBootContext ctx, GuiceFilter filter) {
        serv = new Server(ctx.getWebServerPort());

        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addFilter(new FilterHolder(filter), "/*", EnumSet.allOf(DispatcherType.class));
        serv.setHandler(handler);
    }

    @Override
    public void start() {
        try {
            serv.start();
            serv.join();
        } catch (Exception ex) {
            throw new RuntimeException("Jetty cannot start.", ex);
        }
    }

    @Override
    public void stop() {
        try {
            serv.stop();
        } catch (Exception ex) {
            throw new RuntimeException("Jetty cannot stop.", ex);
        }
    }

    @Override
    public boolean isRunning() {
        return serv.isRunning();
    }

}
