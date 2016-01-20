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

import com.diamondboot.serverproxy.instance.MinecraftInstanceManager;
import com.diamondboot.core.metadata.MinecraftInstanceMetadata;
import com.diamondboot.serverproxy.MinecraftProxyFactory;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("/instances")
public class InstanceService {
    
    private final MinecraftInstanceManager instances;
    private final MinecraftProxyFactory factory;
    
    @Inject
    public InstanceService(MinecraftInstanceManager instances,
            MinecraftProxyFactory factory) {
        this.instances = instances;
        this.factory = factory;
    }
    
    @GET
    public List<MinecraftInstanceMetadata> getInstances() {
        try {
            return instances.getInstances();
        } catch (IOException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @GET @Path("{id}")
    public MinecraftInstanceMetadata getInstance(@PathParam("id") String id) {
        try {
            return instances.getInstance(id);
        } catch (IOException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @POST @Path("{id}")
    public Response performInstanceAction(@PathParam("id") String id, @FormParam("action") String action) {
        Response r  = Response.status(Response.Status.BAD_REQUEST).build();
        
        try {
            switch(action) {
                case "start":
                    factory.get(id).start();
                    break;
                case "stop":
                    factory.get(id).stop();
                    break;
                case "create":
                    instances.newInstance(id);
                    break;
            }
            r = Response.ok().build();
        } catch (IOException ex) {
            throw new WebApplicationException(ex);
        }
        
        return r;
    }
    
}
