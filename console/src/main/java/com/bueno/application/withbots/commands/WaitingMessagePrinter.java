package com.bueno.application.withbots.commands;

import com.bueno.application.utils.Command;

public class WaitingMessagePrinter implements Command<Void> {

    /*
     * @ also
     * 
     * @ public normal_behavior
     * 
     * @ ensures \result == null;
     * 
     * @
     */
    @Override
    public Void execute() {
        System.out.println("\nSimulating... it may take a while: ");
        return null;
    }
}
