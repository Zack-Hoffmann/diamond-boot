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
package com.diamondboot.modules.events;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class DiamondBootEvent {

    public static final String ALL_INSTANCES = "#ALL#";

    public static DiamondBootEvent newEvent(String targetInstance, String content) {
        DiamondBootEvent e = new DiamondBootEvent();
        e.setContent(content);
        e.setTargetInstance(targetInstance);
        return e;
    }

    public static DiamondBootEvent newAllInstanceEvent(String content) {
        return newEvent(ALL_INSTANCES, content);
    }

    private String targetInstance;
    private String content;

    public String getTargetInstance() {
        return targetInstance;
    }

    public void setTargetInstance(String targetInstance) {
        this.targetInstance = targetInstance;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
