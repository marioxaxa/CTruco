package com.bueno.application.withbots.features;

import com.bueno.application.withbots.commands.BotsAvailablePrinter;
import com.bueno.application.withbots.commands.BotOptionReader;
import com.bueno.application.withbots.commands.EvaluateBotsPrinter;
import com.bueno.application.withbots.commands.WaitingMessagePrinter;
import com.bueno.domain.usecases.bot.providers.BotManagerService;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.game.dtos.EvaluateResultsDto;
import com.bueno.domain.usecases.game.usecase.EvaluateBotsUseCase;

import java.util.List;

public class EvaluateBot {

    /*
     * @ public invariant repository != null;
     * 
     * @ public invariant botApi != null;
     * 
     * @ public invariant providerService != null;
     * 
     * @
     */
    private final RemoteBotRepository repository;
    private final RemoteBotApi botApi;
    private final BotManagerService providerService;

    /*
     * @ public normal_behavior
     * 
     * @ requires repository != null;
     * 
     * @ requires botApi != null;
     * 
     * @ requires providerService != null;
     * 
     * @ ensures this.repository == repository;
     * 
     * @ ensures this.botApi == botApi;
     * 
     * @ ensures this.providerService == providerService;
     * 
     * @
     */
    public EvaluateBot(RemoteBotRepository repository, RemoteBotApi botApi, BotManagerService providerService) {
        this.repository = repository;
        this.botApi = botApi;
        this.providerService = providerService;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures true;
     * 
     * @
     */
    public void againstAll() {
        final var botNames = providerService.providersNames();

        printAvailableBots(botNames);
        String botToEvaluateName = botNames.get(scanBotOption(botNames) - 1);

        printWaitingMessage();

        printResultEvaluateBot(getEvaluateResultsDto(botToEvaluateName, botNames), botToEvaluateName);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires botToEvaluateName != null;
     * 
     * @ requires botNames != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    private EvaluateResultsDto getEvaluateResultsDto(String botToEvaluateName, List<String> botNames) {
        EvaluateBotsUseCase useCase = new EvaluateBotsUseCase(repository, botApi, providerService);
        return useCase.evaluate(botNames, botToEvaluateName);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires botNames != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    private void printAvailableBots(List<String> botNames) {
        BotsAvailablePrinter printer = new BotsAvailablePrinter(botNames);
        printer.execute();
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires botNames != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    private int scanBotOption(List<String> botNames) {
        BotOptionReader scanOptions = new BotOptionReader(botNames);
        return scanOptions.execute();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures true;
     * 
     * @
     */
    private void printWaitingMessage() {
        WaitingMessagePrinter messagePrinter = new WaitingMessagePrinter();
        messagePrinter.execute();
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires resultsDto != null;
     * 
     * @ requires botName != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    private void printResultEvaluateBot(EvaluateResultsDto resultsDto, String botName) {
        EvaluateBotsPrinter printer = new EvaluateBotsPrinter(resultsDto, botName);
        printer.execute();
    }
}
