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

import com.diamondboot.web.DiamondBootWebServer;
import com.diamondboot.web.JettyDiamondBootWebServer;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceFilter;
import javax.servlet.Filter;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class WebServerModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(DiamondBootWebServer.class).to(JettyDiamondBootWebServer.class).in(Scopes.SINGLETON);
        bind(Filter.class).annotatedWith(Names.named("guiceFilter")).to(GuiceFilter.class);
    }
    
}
