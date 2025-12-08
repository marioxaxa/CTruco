package com.bueno.domain.usecases.game.usecase;

import com.bueno.domain.usecases.bot.providers.BotManagerService;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.game.dtos.BotRankInfoDto;
import com.bueno.domain.usecases.game.dtos.PlayWithBotsDto;
import com.bueno.domain.usecases.game.service.SimulationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service
public class RankBotsUseCase {
    public static final int TIMES_RANK = 7;

    /* @ spec_public @ */
    private final RemoteBotRepository remoteBotRepository;
    /* @ spec_public @ */
    private final RemoteBotApi remoteBotApi;
    /* @ spec_public @ */
    private final List<String> botNames;
    /* @ spec_public @ */
    private final BotManagerService botManagerService;
    /* @ spec_public @ */
    private boolean isRanking = false;
    /* @ spec_public @ */
    private boolean hasRank = false;
    /* @ spec_public @ */
    private List<BotRankInfoDto> rank = new ArrayList<>();
    /* @ spec_public @ */
    Map<String, Long> resultsMap = new HashMap<>();
    private long start;

    /*
     * @ public invariant remoteBotRepository != null;
     * 
     * @ public invariant remoteBotApi != null;
     * 
     * @ public invariant botManagerService != null;
     * 
     * @ public invariant rank != null;
     * 
     * @ public invariant resultsMap != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires remoteBotRepository != null;
     * 
     * @ requires remoteBotApi != null;
     * 
     * @ ensures this.remoteBotRepository == remoteBotRepository;
     * 
     * @ ensures this.remoteBotApi == remoteBotApi;
     * 
     * @ ensures this.botManagerService != null;
     * 
     * @
     */
    public RankBotsUseCase(RemoteBotRepository remoteBotRepository, RemoteBotApi remoteBotApi) {
        this.remoteBotRepository = remoteBotRepository;
        this.remoteBotApi = remoteBotApi;
        botManagerService = new BotManagerService(remoteBotRepository, remoteBotApi);
        botNames = botManagerService.providersNames();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ ensures isRanking == false;
     * 
     * @
     */
    public Map<String, Long> rankAll() {
        setRank(new ArrayList<>());
        setIsRanking(true);
        start = System.currentTimeMillis();
        System.out.println("simulando");
        botNames.forEach(this::playAgainstAll);
        resultsHandler(resultsMap, botNames);
        System.out.println("terminou");
        setIsRanking(false);
        return resultsMap;
    }

    private void resultsHandler(Map<String, Long> result, List<String> botNames) {
        var sortedRankMap = result.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        long rankNumber = 1;
        for (Map.Entry<String, Long> bot : sortedRankMap.entrySet()) {
            rank.add(new BotRankInfoDto(bot.getKey(), bot.getValue(), rankNumber));
            rankNumber++;
        }
    }

    private void playAgainstAll(String botName) {

        UUID uuidBotToEvaluate = UUID.randomUUID();
        long botWins = botNames.stream()
                .filter(opponentName -> isNotEvaluatedBot(opponentName, botName))
                .map(opponent -> runSimulations(opponent, botName, uuidBotToEvaluate))
                .flatMap(List::stream)
                .filter(result -> result.name().equals(botName))
                .count();

        resultsMap.put(botName, botWins);
    }

    private boolean isNotEvaluatedBot(String opponentName, String botToEvaluateName) {
        return !opponentName.equals(botToEvaluateName);
    }

    private List<PlayWithBotsDto> runSimulations(String challengedBotName, String botToEvaluateName,
            UUID uuidBotToEvaluate) {
        final var simulator = new SimulationService(remoteBotRepository, remoteBotApi, botManagerService);
        return simulator.runInParallel(uuidBotToEvaluate, botToEvaluateName, UUID.randomUUID(), challengedBotName,
                TIMES_RANK);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.isRanking == ranking;
     * 
     * @
     */
    public void setIsRanking(boolean ranking) {
        isRanking = ranking;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == isRanking;
     * 
     * @
     */
    public boolean isRanking() {
        return isRanking;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == hasRank;
     * 
     * @
     */
    public boolean hasRank() {
        return hasRank;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.hasRank == hasRank;
     * 
     * @
     */
    public void setHasRank(boolean hasRank) {
        this.hasRank = hasRank;
    }

    public long getProcessingTime() {
        return System.currentTimeMillis() - start;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == rank;
     * 
     * @
     */
    public List<BotRankInfoDto> getRank() {
        return rank;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires rank != null;
     * 
     * @ ensures this.rank == rank;
     * 
     * @
     */
    public void setRank(List<BotRankInfoDto> rank) {
        this.rank = rank;
    }
}
