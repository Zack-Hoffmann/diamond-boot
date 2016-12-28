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
package com.diamondboot.core.status;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class DiamondBootStatus {

    public static class InstanceStatus {

        private String state;
        private String version;
        private int playersOnline;
        private int maxPlayersOnline;
        private int playersBanned;
        private long upTime;
        private long dayTime;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getPlayersOnline() {
            return playersOnline;
        }

        public void setPlayersOnline(int playersOnline) {
            this.playersOnline = playersOnline;
        }

        public int getMaxPlayersOnline() {
            return maxPlayersOnline;
        }

        public void setMaxPlayersOnline(int maxPlayersOnline) {
            this.maxPlayersOnline = maxPlayersOnline;
        }

        public int getPlayersBanned() {
            return playersBanned;
        }

        public void setPlayersBanned(int playersBanned) {
            this.playersBanned = playersBanned;
        }

        public long getUpTime() {
            return upTime;
        }

        public void setUpTime(long upTime) {
            this.upTime = upTime;
        }

        public long getDayTime() {
            return dayTime;
        }

        public void setDayTime(long dayTime) {
            this.dayTime = dayTime;
        }

    }

    private String version;
    private String hostname;
    private int port;
    private final Map<String, InstanceStatus> instances = Maps.newHashMap();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public Map<String, InstanceStatus> getInstances() {
        return instances;
    }

}
