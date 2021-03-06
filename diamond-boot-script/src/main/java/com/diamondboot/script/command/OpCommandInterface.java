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
package com.diamondboot.script.command;

import java.util.List;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public interface OpCommandInterface {
    List<String> list() throws InterruptedException;
    List<String> banlist(String option);
    Long time(String option, String value);
    int maxPlayers() throws InterruptedException;
    void stop();
    // TODO finish command list
}
