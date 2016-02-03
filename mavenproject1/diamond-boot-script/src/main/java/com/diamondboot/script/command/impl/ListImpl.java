/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script.command.impl;

import com.diamondboot.script.command.AbstractMinecraftCommand;
import com.diamondboot.script.command.MinecraftCommand;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class ListImpl extends AbstractMinecraftCommand<List<String>> {

    public ListImpl(String instance, EventBus bus) {
        super(instance, bus);
    }
    
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public List<String> apply(String... t) {
        List<String> players = Lists.newArrayList();
        invoke();
        int count = -1;
        do {
            String line = takeResponse();
            // TODO log
            Pattern p = Pattern.compile("There are ([\\d]+)/[\\d]+ players online");
            Matcher m = p.matcher(line);
            if (m.find()) {
                count = Integer.parseInt(m.group(1));
            }
        } while (count == -1);
        for (int i = 0; i < count; i++) {
            String line = takeResponse();
            Pattern p = Pattern.compile(": ([\\w]+)$");
            Matcher m = p.matcher(line);
            if (m.find()) {
                players.add(m.group(1));
            }
        }
        return players;
    }

}
