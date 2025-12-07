package com.bueno.application.withbots.commands;

import com.bueno.application.utils.Command;

import java.util.Scanner;

public class NumberOfSimulationsReader implements Command<Integer> {

    /*
     * @ also
     * 
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (java.util.InputMismatchException) true;
     * 
     * @ signals (java.util.NoSuchElementException) true;
     * 
     * @
     */
    @Override
    public Integer execute() {
        final var scanner = new Scanner(System.in);
        System.out.print("Number of simulations: ");
        return scanner.nextInt();
    }
}
