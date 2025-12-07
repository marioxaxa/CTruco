package com.bueno.application.withbots.features;

import com.bueno.application.withbots.commands.BotRankPrinter;
import com.bueno.application.withbots.commands.WaitingMessagePrinter;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.game.usecase.RankBotsUseCase;

import java.util.*;
import java.util.stream.Collectors;

public class RankBots {

    /*
     * @ public invariant repository != null;
     * 
     * @ public invariant api != null;
     * 
     * @
     */
    private final RemoteBotRepository repository;
    private final RemoteBotApi api;

    /*
     * @ public normal_behavior
     * 
     * @ requires repository != null;
     * 
     * @ requires api != null;
     * 
     * @ ensures this.repository == repository;
     * 
     * @ ensures this.api == api;
     * 
     * @
     */
    public RankBots(RemoteBotRepository repository, RemoteBotApi api) {
        this.repository = repository;
        this.api = api;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures true;
     * 
     * @
     */
    public void allBots() {

        final var useCase = new RankBotsUseCase(repository, api);

        showWaitingMessage();

        Map<String, Long> rankInfo = useCase.rankAll();
        rankInfo = sortByValueDescending(rankInfo);

        printRank(rankInfo);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires rankInfo != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    private void printRank(Map<String, Long> rankInfo) {
        BotRankPrinter printer = new BotRankPrinter(rankInfo);
        printer.execute();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures true;
     * 
     * @
     */
    private void showWaitingMessage() {
        WaitingMessagePrinter messagePrinter = new WaitingMessagePrinter();
        messagePrinter.execute();
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires contentToSort != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    private Map<String, Long> sortByValueDescending(Map<String, Long> contentToSort) {
        return contentToSort.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
