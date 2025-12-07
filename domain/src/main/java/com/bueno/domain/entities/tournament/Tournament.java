package com.bueno.domain.entities.tournament;

import com.bueno.domain.usecases.bot.providers.BotManagerService;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;

import java.util.*;

public class Tournament {
    /* @ spec_public @ */
    private final UUID tournamentUUID;
    /* @ spec_public @ */
    private final int size;
    /* @ spec_public @ */
    private final List<String> participantNames;
    /* @ spec_public @ */
    private final int times;
    /* @ spec_public @ */
    private final int finalAndThirdPlaceMatchTimes;
    /* @ spec_public @ */
    private final List<Match> matches;

    /*
     * @ public invariant tournamentUUID != null;
     * 
     * @ public invariant matches != null;
     * 
     * @ public invariant participantNames != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires participantNames != null;
     * 
     * @ ensures this.participantNames == participantNames;
     * 
     * @ ensures this.size == size;
     * 
     * @ ensures this.times == times;
     * 
     * @ ensures this.finalAndThirdPlaceMatchTimes == finalAndThirdPlaceMatchTimes;
     * 
     * @ ensures this.tournamentUUID != null;
     * 
     * @ ensures this.matches != null && this.matches.isEmpty();
     * 
     * @
     */
    public Tournament(List<String> participantNames, int size, int times, int finalAndThirdPlaceMatchTimes) {
        this.times = times;
        this.finalAndThirdPlaceMatchTimes = finalAndThirdPlaceMatchTimes;
        this.tournamentUUID = UUID.randomUUID();
        this.size = size;
        this.participantNames = participantNames;
        this.matches = new ArrayList<>();
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires tournamentUUID != null;
     * 
     * @ requires participantNames != null;
     * 
     * @ requires matches != null;
     * 
     * @ ensures this.tournamentUUID == tournamentUUID;
     * 
     * @ ensures this.participantNames == participantNames;
     * 
     * @ ensures this.size == size;
     * 
     * @ ensures this.times == times;
     * 
     * @ ensures this.matches.size() == matches.size();
     * 
     * @
     */
    public Tournament(UUID tournamentUUID, List<String> participantNames, List<Match> matches, int size, int times,
            int finalAndThirdPlaceMatchTimes) {
        this.tournamentUUID = tournamentUUID;
        this.size = size;
        this.matches = matches.stream().sorted().toList();
        this.participantNames = participantNames;
        this.times = times;
        this.finalAndThirdPlaceMatchTimes = times;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires api != null;
     * 
     * @ requires repository != null;
     * 
     * @ requires botManagerService != null;
     * 
     * @
     */
    public void playAllAvailable(RemoteBotApi api, RemoteBotRepository repository,
            BotManagerService botManagerService) {
        for (Match m : matches) {
            if (m.isAvailable()) {
                m.play(repository, api, botManagerService, times);
            }
        }

    }

    /*
     * @ public normal_behavior
     * 
     * @ requires matchUuid != null;
     * 
     * @ requires api != null;
     * 
     * @ requires botManagerService != null;
     * 
     * @ requires repository != null;
     * 
     * @
     */
    public void playByMatchUuid(UUID matchUuid, RemoteBotApi api, BotManagerService botManagerService,
            RemoteBotRepository repository, int numberOfSimulations) {
        matches.stream()
                .filter(match -> match.getId().equals(matchUuid))
                .findFirst()
                .ifPresentOrElse(match -> match.play(repository, api, botManagerService, numberOfSimulations),
                        () -> System.out.println("No match found"));
    }

    public void setAvailableMatches() {
        matches.forEach(Match::setAvailableState);
    }

    // TODO - mudar p/ programação declarativa
    /*
     * @ public normal_behavior
     * 
     * @ ensures matches.size() == \old(matches.size()) + size;
     * 
     * @
     */
    public void insertMatches() {
        for (int i = 0; i < size; i++)
            matches.add(new Match(UUID.randomUUID(),
                    i + 1));
    }

    // TODO - mudar p/ programação declarativa
    /*
     * @ public normal_behavior
     * 
     * @ requires matches.size() >= size / 2;
     * 
     * @ requires participantNames.size() >= size;
     * 
     * @
     */
    public void insertParticipants() {
        int participantsIndex = 0;
        for (int i = 0; i < size / 2; i++) {
            matches.get(i).setP1Name(participantNames.get(participantsIndex));
            matches.get(i).setP2Name(participantNames.get(participantsIndex + 1));
            participantsIndex = participantsIndex + 2;
        }
    }

    // TODO - mudar p/ programação declarativa
    /*
     * @ public normal_behavior
     * 
     * @ requires matches.size() >= size;
     * 
     * @
     */
    public void setNextMatches() {
        int next = size / 2 - 1;
        for (int i = 0; i < size - 2; i++) {
            if (i % 2 == 0)
                next++;
            matches.get(i).setNext(matches.get(next));
        }
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires cacheMatches != null;
     * 
     * @ requires matches.size() >= size - 2;
     * 
     * @
     */
    public void refreshMatches(Map<UUID, Match> cacheMatches) {
        final int firstSemiFinalIndex = size - 4;
        final int secondSemiFinalIndex = size - 3;
        Match semiFinal1 = matches.get(firstSemiFinalIndex);
        Match semiFinal2 = matches.get(secondSemiFinalIndex);
        String semiFinalLoser1 = semiFinal1.getLoserName();
        String semiFinalLoser2 = semiFinal2.getLoserName();
        matches.forEach(m -> updateTournamentSetting(cacheMatches, m, semiFinalLoser1, semiFinalLoser2));
    }

    private void updateTournamentSetting(Map<UUID, Match> cacheMatches,
            Match m,
            String semiFinalLoser1,
            String semiFinalLoser2) {
        if (isThirdPlaceMatch(m)) {
            m.setP1Name(semiFinalLoser1);
            m.setP2Name(semiFinalLoser2);
        }
        m.setAvailableState();
        boolean nextMatchChanged = m.setWinnerToNextBracket();
        addToCache(m, cacheMatches, nextMatchChanged);
    }

    private boolean isThirdPlaceMatch(Match m) {
        return m.getMatchNumber() == size;
    }

    public void setAvailableOnes() {
        matches.forEach(Match::setAvailableState);
    }

    private void addToCache(Match match, Map<UUID, Match> cacheMatches, boolean nextMatchChanged) {
        if (nextMatchChanged) {
            nextMatchChanged = false;
            addToCache(match.getNext(), cacheMatches, nextMatchChanged);
        }
        if (!cacheMatches.containsKey(match.getId()))
            cacheMatches.put(match.getId(), match);
        else
            cacheMatches.get(match.getId()).setAvailableState();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == size;
     * 
     * @
     */
    public /* @ pure @ */ int getSize() {
        return size;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == matches;
     * 
     * @
     */
    public /* @ pure @ */ List<Match> getMatches() {
        return matches;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public /* @ pure @ */ List<Match> getAvailableMatches() {
        return matches.stream().filter(Match::isAvailable).toList();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == times;
     * 
     * @
     */
    public /* @ pure @ */ int getTimes() {
        return times;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == tournamentUUID;
     * 
     * @
     */
    public /* @ pure @ */ UUID getTournamentUUID() {
        return tournamentUUID;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == participantNames;
     * 
     * @
     */
    public /* @ pure @ */ List<String> getParticipantNames() {
        return participantNames;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == finalAndThirdPlaceMatchTimes;
     * 
     * @
     */
    public /* @ pure @ */ int getFinalAndThirdPlaceMatchTimes() {
        return finalAndThirdPlaceMatchTimes;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "tournamentUUID=" + tournamentUUID +
                ", size=" + size +
                ", matches="
                + (matches == null ? " null" : matches.stream().map(match -> "\n\t" + match.toString()).toList()) +
                '}';
    }
}
