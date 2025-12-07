package com.bueno.application.withbots.commands;

import com.bueno.application.utils.Command;
import com.bueno.domain.usecases.game.dtos.PlayWithBotsDto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayWithBotsPrinter implements Command<Void> {
    /*
     * @ public invariant numberOfGames >= 0;
     * 
     * @ public invariant computingTime >= 0;
     * 
     * @ public invariant results != null;
     * 
     * @
     */
    int numberOfGames;
    long computingTime;
    List<PlayWithBotsDto> results;

    /*
     * @ public normal_behavior
     * 
     * @ requires numberOfGames >= 0;
     * 
     * @ requires computingTime >= 0;
     * 
     * @ requires results != null;
     * 
     * @ ensures this.numberOfGames == numberOfGames;
     * 
     * @ ensures this.computingTime == computingTime;
     * 
     * @ ensures this.results == results;
     * 
     * @
     */
    public PlayWithBotsPrinter(int numberOfGames, long computingTime, List<PlayWithBotsDto> results) {
        this.numberOfGames = numberOfGames;
        this.computingTime = computingTime;
        this.results = results;
    }

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
        System.out.println("\n================================================================");
        System.out.println("Time to compute " + numberOfGames + " games: " + computingTime + "ms.\n");
        results.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .forEach((bot, wins) -> System.out.println(bot.name() + ": " + wins));
        System.out.println("================================================================");
        return null;
    }
}
