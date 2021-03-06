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

import com.diamondboot.web.InstanceService;
import com.diamondboot.web.StatusService;
import com.diamondboot.web.VersionService;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.eclipse.jetty.servlets.CrossOriginFilter;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ServletsModule extends ServletModule {
    
    @Override
    protected void configureServlets() {
        bind(InstanceService.class);
        bind(VersionService.class);
        bind(StatusService.class);
        bind(GuiceContainer.class);
        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
        bind(CrossOriginFilter.class).in(Scopes.SINGLETON);
        
        serve("/services/*").with(GuiceContainer.class);
        filter("/*").through(CrossOriginFilter.class, ImmutableMap.of(
                "allowedOrigins", "*",
                "allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD"));
    }  
    
}
