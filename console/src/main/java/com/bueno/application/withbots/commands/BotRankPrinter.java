package com.bueno.application.withbots.commands;

import com.bueno.application.utils.Command;

import java.util.Map;

public class BotRankPrinter implements Command<Void> {
    /* @ public invariant botRankMap != null; @ */
    Map<String, Long> botRankMap;

    /*
     * @ public normal_behavior
     * 
     * @ requires botRank != null;
     * 
     * @ ensures this.botRankMap == botRank;
     * 
     * @
     */
    public BotRankPrinter(Map<String, Long> botRank) {
        this.botRankMap = botRank;
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
        System.out.println("Rank Of Bots");
        int rank = 0;
        for (var bot : botRankMap.entrySet()) {
            rank++;
            System.out.println("[" + rank + "] " + bot.getKey());
        }
        return null;
    }
}
