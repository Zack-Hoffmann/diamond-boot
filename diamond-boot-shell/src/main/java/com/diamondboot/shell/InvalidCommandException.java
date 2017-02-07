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
public class InvalidCommandException extends Exception {

    public InvalidCommandException() {
    }

    public InvalidCommandException(String string) {
        super(string);
    }

    public InvalidCommandException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public InvalidCommandException(Throwable thrwbl) {
        super(thrwbl);
    }

}
