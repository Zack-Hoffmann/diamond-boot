/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.script.command;

import java.util.function.Function;

/**
 *
 * @author Zack Hoffmann <zachary.hoffmann@gmail.com>
 */
public interface MinecraftCommand<R> extends Function<String[], R> {

    String getName();
}
