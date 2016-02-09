/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.serverproxy.instance.properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class MinecraftServerProperty {

    public static enum Type {

        BOOLEAN,
        NUMERIC,
        TEXT,
        SELECT
    }

    public static class SelectProperty extends MinecraftServerProperty {

        private Map<String, String> options;

        public SelectProperty() {
            this.options = Maps.newHashMap();
        }

        public SelectProperty(String shortName, String longName, Type type, Map<String, String> options) {
            super(shortName, longName, type);
            this.options = options;
        }

        public Map<String, String> getOptions() {
            return options;
        }

        public void setOptions(Map<String, String> options) {
            this.options = options;
        }

    }

    private String shortName;
    private String longName;
    private Type type;

    public MinecraftServerProperty() {
    }

    public MinecraftServerProperty(String shortName, String longName, Type type) {
        this.shortName = shortName;
        this.longName = longName;
        this.type = type;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
