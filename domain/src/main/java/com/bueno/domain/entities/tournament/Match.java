package com.bueno.domain.entities.tournament;

import com.bueno.domain.usecases.bot.providers.BotManagerService;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.game.dtos.PlayWithBotsResultsDto;
import com.bueno.domain.usecases.game.usecase.PlayWithBotsUseCase;

import java.security.Timestamp;
import java.util.UUID;

public class Match implements Comparable {
    /* @ spec_public @ */
    private final UUID id;
    /* @ spec_public @ */
    private final int matchNumber;
    /* @ spec_public @ */
    private String p1Name;
    /* @ spec_public @ */
    private String p2Name;
    /* @ spec_public @ */
    private boolean isAvailable;
    /* @ spec_public @ */
    private String winnerName;
    /* @ spec_public @ */
    private long p1Score;
    /* @ spec_public @ */
    private long p2Score;
    /* @ spec_public @ */
    private long timeToExecute;
    /* @ spec_public nullable @ */
    private Match next;

    /* @ public invariant id != null; @ */

    /*
     * @ public normal_behavior
     * 
     * @ requires id != null;
     * 
     * @ ensures this.id == id;
     * 
     * @ ensures this.matchNumber == matchNumber;
     * 
     * @ ensures this.p1Name == p1Name;
     * 
     * @ ensures this.p2Name == p2Name;
     * 
     * @ ensures this.isAvailable == isAvailable;
     * 
     * @ ensures this.winnerName == winnerName;
     * 
     * @ ensures this.p1Score == p1Score;
     * 
     * @ ensures this.p2Score == p2Score;
     * 
     * @ ensures this.timeToExecute == timeToExecute;
     * 
     * @ ensures this.next == next;
     * 
     * @
     */
    public Match(UUID id, int matchNumber,
            String p1Name,
            String p2Name,
            boolean isAvailable,
            String winnerName,
            long p1Score,
            long p2Score,
            long timeToExecute,
            Match next) {
        this.id = id;
        this.matchNumber = matchNumber;
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.isAvailable = isAvailable;
        this.winnerName = winnerName;
        this.p1Score = p1Score;
        this.p2Score = p2Score;
        this.timeToExecute = timeToExecute;
        this.next = next;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires id != null;
     * 
     * @ ensures this.id == id;
     * 
     * @ ensures this.matchNumber == matchNumber;
     * 
     * @
     */
    public Match(UUID id, int matchNumber) {
        this.id = id;
        this.matchNumber = matchNumber;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires repository != null;
     * 
     * @ requires botApi != null;
     * 
     * @ requires botManagerService != null;
     * 
     * @ requires times > 0;
     * 
     * @
     */
    public void play(RemoteBotRepository repository,
            RemoteBotApi botApi,
            BotManagerService botManagerService,
            int times) {
        final PlayWithBotsUseCase useCase = new PlayWithBotsUseCase(repository, botApi, botManagerService);
        PlayWithBotsResultsDto results = useCase.playWithBots(UUID.randomUUID(), p1Name, UUID.randomUUID(), p2Name,
                times / 2);
        PlayWithBotsResultsDto resultsChangingTheFirstPlayer = useCase.playWithBots(UUID.randomUUID(), p2Name,
                UUID.randomUUID(), p1Name, times / 2);
        long p1Wins = results.info()
                .stream()
                .filter(info -> info.name().equals(p1Name)).count()
                +
                resultsChangingTheFirstPlayer.info()
                        .stream()
                        .filter(info -> info.name().equals(p1Name)).count();
        long p2Wins = times - p1Wins;
        timeToExecute = results.timeToExecute();
        p1Score = p1Wins;
        p2Score = p2Wins;
        if (p1Wins > times / 2) {
            winnerName = p1Name;
        } else
            winnerName = p2Name;
        isAvailable = false;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == (next != null && winnerName != null &&
     * ((shouldBePlayerTwo() && next.p2Name == null) || (!shouldBePlayerTwo() &&
     * next.p1Name == null)));
     * 
     * @
     */
    public boolean setWinnerToNextBracket() {
        if (next == null)
            return false;
        if (winnerName == null)
            return false;

        if (shouldBePlayerTwo()) {
            if (next.p2Name != null)
                return false;

            next.p2Name = winnerName;
            return true;
        }

        if (next.p1Name != null)
            return false;

        next.p1Name = winnerName;
        return true;
    }

    private boolean shouldBePlayerTwo() {
        return matchNumber % 2 == 0;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures isAvailable == (p1Name != null && p2Name != null && winnerName ==
     * null);
     * 
     * @
     */
    public void setAvailableState() {
        isAvailable = isPlayableMatch();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == (p1Name != null && p2Name != null && winnerName ==
     * null);
     * 
     * @
     */
    private /* @ pure @ */ boolean isPlayableMatch() {
        return p1Name != null && p2Name != null && winnerName == null;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == id;
     * 
     * @
     */
    public /* @ pure @ */ UUID getId() {
        return id;
    }

    public void setP1Name(String p1Name) {
        this.p1Name = p1Name;
    }

    public void setP2Name(String p2Name) {
        this.p2Name = p2Name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public Match getNext() {
        return next;
    }

    public void setNext(Match next) {
        this.next = next;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public String getP1Name() {
        return p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }

    public long getTimeToExecute() {
        return timeToExecute;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getLoserName() {
        if (p1Name == null || p2Name == null || winnerName == null)
            return null;

        return (p1Name.equals(winnerName) ? p2Name : p1Name);
    }

    public long getP1Score() {
        return p1Score;
    }

    public long getP2Score() {
        return p2Score;
    }

    @Override
    public String toString() {
        return "MatchInfo{" +
                "uuid= " + id +
                ", number= " + matchNumber +
                ", p1Name='" + p1Name + '\'' +
                ", p2Name='" + p2Name + '\'' +
                ", available=" + isAvailable +
                ", winnerName='" + winnerName + '\'' +
                ", p1Score=" + p1Score +
                ", p2Score=" + p2Score +
                ", nextMatch=" + (next == null ? ("null") : (next.id.toString()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Match match = (Match) o;
        return id.equals(match.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || this.getClass() != o.getClass())
            throw new RuntimeException();
        Match match = (Match) o;
        return Integer.compare(this.matchNumber, match.matchNumber);
    }

}
