package com.bueno.application.main.commands;

import com.bueno.application.utils.Command;

public class InitialMenuPrinter implements Command<Void> {
    /*
     * @ also
     * 
     * @ public behavior
     * 
     * @ ensures \result == null;
     * 
     * @
     */
    @Override
    public Void execute() {
        System.out.println("=+=+= CTRUCO CONSOLE =+=+=");
        return null;
    }
}
