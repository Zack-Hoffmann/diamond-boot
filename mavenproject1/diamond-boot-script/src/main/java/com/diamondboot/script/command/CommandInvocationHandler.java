/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script.command;

import com.diamondboot.core.utility.Exceptions;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public class CommandInvocationHandler implements InvocationHandler {

    private static final String INVALID_PARAM_ERROR = "Method invocation is not defined for proxy type '%s' and method '%s'.";

    private final MinecraftCommandMap commands;

    public CommandInvocationHandler(MinecraftCommandMap commands) {
        this.commands = commands;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] os) throws Throwable {
        if (o instanceof OpCommandInterface && commands.containsKey(method.getName())) {
            // TODO log propper
            System.out.println("Invoking " + method.getName() + "...");
            String[] args = new String[0];
            if (os != null) {
                args = Arrays.asList(os).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
                        .toArray(new String[os.length]);
            }
            return commands.get(method.getName()).apply(args);
        } else if (method.getName().equals("toString")) {
            return o.getClass().toString();
        } else {
            throw Exceptions.invalidParameter(INVALID_PARAM_ERROR, o.getClass(), method.getName());
        }
    }

}
