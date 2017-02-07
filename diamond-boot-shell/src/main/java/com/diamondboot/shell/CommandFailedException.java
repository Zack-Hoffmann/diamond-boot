/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diamondboot.shell;

/**
 *
 * @author Zack Hoffmann
 */
public class CommandFailedException extends Exception {

    public CommandFailedException() {
    }

    public CommandFailedException(String string) {
        super(string);
    }

    public CommandFailedException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public CommandFailedException(Throwable thrwbl) {
        super(thrwbl);
    }

}
