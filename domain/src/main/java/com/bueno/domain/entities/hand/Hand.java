/*
 *  Copyright (C) 2022 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.domain.entities.hand;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.GameRuleViolationException;
import com.bueno.domain.entities.hand.states.*;
import com.bueno.domain.entities.intel.Event;
import com.bueno.domain.entities.intel.Intel;
import com.bueno.domain.entities.intel.PossibleAction;
import com.bueno.domain.entities.player.Player;

import java.util.*;

public class Hand {

    /*
     * @ public invariant vira != null;
     * 
     * @ public invariant dealtCards != null;
     * 
     * @ public invariant openCards != null;
     * 
     * @ public invariant roundsPlayed != null;
     * 
     * @ public invariant history != null;
     * 
     * @ public invariant possibleActions != null;
     * 
     * @ public invariant firstToPlay != null;
     * 
     * @ public invariant lastToPlay != null;
     * 
     * @ public invariant state != null;
     * 
     * @
     */
    /* @ spec_public @ */
    private final Card vira;
    /* @ spec_public @ */
    private final List<Card> dealtCards;
    /* @ spec_public @ */
    private final List<Card> openCards;
    /* @ spec_public @ */
    private final List<Round> roundsPlayed;
    /* @ spec_public @ */
    private final List<Intel> history;
    /* @ spec_public @ */
    private EnumSet<PossibleAction> possibleActions;

    /* @ spec_public @ */
    private Player firstToPlay;
    /* @ spec_public @ */
    private Player lastToPlay;
    private Player currentPlayer;
    private Player lastBetRaiser;
    private Player eventPlayer;

    private Card cardToPlayAgainst;
    private HandPoints points;
    private HandPoints pointsProposal;

    private HandResult result;
    /* @ spec_public @ */
    private HandState state;

    // This method must only be used to recovery the object state from database. Do
    // not use for creating a new hand.
    // To create a hand, use the Game class, since it is its bounded context border.
    /*
     * @ public normal_behavior
     * 
     * @ requires vira != null;
     * 
     * @ requires dealtCards != null;
     * 
     * @ requires openCards != null;
     * 
     * @ requires roundsPlayed != null;
     * 
     * @ requires history != null;
     * 
     * @ requires possibleActions != null;
     * 
     * @ requires firstToPlay != null;
     * 
     * @ requires lastToPlay != null;
     * 
     * @ requires stateName != null;
     * 
     * @ ensures this.vira == vira;
     * 
     * @ ensures this.firstToPlay == firstToPlay;
     * 
     * @ ensures this.lastToPlay == lastToPlay;
     * 
     * @
     */
    public Hand(Card vira, List<Card> dealtCards, List<Card> openCards, List<Round> roundsPlayed, List<Intel> history,
            EnumSet<PossibleAction> possibleActions, Player firstToPlay, Player lastToPlay, Player currentPlayer,
            Player lastBetRaiser, Player eventPlayer, Card cardToPlayAgainst, HandPoints points,
            HandPoints pointsProposal, HandResult result, String stateName) {
        this.vira = vira;
        this.dealtCards = new ArrayList<>(dealtCards);
        this.openCards = new ArrayList<>(openCards);
        this.roundsPlayed = new ArrayList<>(roundsPlayed);
        this.history = new ArrayList<>(history);
        this.possibleActions = EnumSet.copyOf(possibleActions);
        this.firstToPlay = firstToPlay;
        this.lastToPlay = lastToPlay;
        this.currentPlayer = currentPlayer;
        this.lastBetRaiser = lastBetRaiser;
        this.eventPlayer = eventPlayer;
        this.cardToPlayAgainst = cardToPlayAgainst;
        this.points = points;
        this.pointsProposal = pointsProposal;
        this.result = result;
        this.state = stateFromString(stateName);
    }

    /*
     * @ private normal_behavior
     * 
     * @ requires stateName != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    private HandState stateFromString(String stateName) {
        return switch (stateName) {
            case "DONE" -> new Done(this);
            case "NOCARD" -> new NoCard(this);
            case "ONECARD" -> new OneCard(this);
            case "WAITINGMAODEONZE" -> new WaitingMaoDeOnze(this);
            case "WAITINGRAISERESPONSE" -> new WaitingRaiseResponse(this);
            default -> throw new IllegalArgumentException("No state for name: " + stateName);
        };
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires firstToPlay != null;
     * 
     * @ requires lastToPlay != null;
     * 
     * @ requires vira != null;
     * 
     * @ ensures this.firstToPlay == firstToPlay;
     * 
     * @ ensures this.lastToPlay == lastToPlay;
     * 
     * @ ensures this.vira == vira;
     * 
     * @
     */
    public Hand(Player firstToPlay, Player lastToPlay, Card vira) {
        this.firstToPlay = Objects.requireNonNull(firstToPlay);
        this.lastToPlay = Objects.requireNonNull(lastToPlay);
        this.vira = Objects.requireNonNull(vira);

        dealtCards = new ArrayList<>();
        dealtCards.add(vira);
        dealtCards.addAll(firstToPlay.getCards());
        dealtCards.addAll(lastToPlay.getCards());

        points = HandPoints.ONE;
        roundsPlayed = new ArrayList<>();
        openCards = new ArrayList<>();
        history = new ArrayList<>();

        addOpenCard(vira);

        if (isMaoDeOnze())
            setMaoDeOnzeMode();
        else
            setOrdinaryMode();

        updateHistory(Event.HAND_START);
    }

    /*
     * @ private normal_behavior
     * 
     * @ ensures state instanceof WaitingMaoDeOnze;
     * 
     * @
     */
    private void setMaoDeOnzeMode() {
        currentPlayer = this.firstToPlay.getScore() == 11 ? this.firstToPlay : this.lastToPlay;
        state = new WaitingMaoDeOnze(this);
    }

    /*
     * @ private normal_behavior
     * 
     * @ ensures state instanceof NoCard;
     * 
     * @
     */
    private void setOrdinaryMode() {
        currentPlayer = this.firstToPlay;
        state = new NoCard(this);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires player != null;
     * 
     * @ requires card != null;
     * 
     * @ requires player.equals(currentPlayer);
     * 
     * @ requires possibleActions.contains(PossibleAction.PLAY);
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (NullPointerException e) player == null || card == null;
     * 
     * @ signals (IllegalArgumentException e) !player.equals(currentPlayer);
     * 
     * @ signals (IllegalStateException e)
     * !possibleActions.contains(PossibleAction.PLAY);
     * 
     * @
     */
    public void playFirstCard(Player player, Card card) {
        final var requester = Objects.requireNonNull(player, "Player must not be null!");
        final var requesterCard = Objects.requireNonNull(card, "Card must not be null!");
        validateRequest(requester, PossibleAction.PLAY);
        eventPlayer = currentPlayer;
        state.playFirstCard(requester, requesterCard);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires player != null;
     * 
     * @ requires cards != null;
     * 
     * @ requires player.equals(currentPlayer);
     * 
     * @ requires possibleActions.contains(PossibleAction.PLAY);
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (NullPointerException e) player == null || cards == null;
     * 
     * @ signals (IllegalArgumentException e) !player.equals(currentPlayer);
     * 
     * @ signals (IllegalStateException e)
     * !possibleActions.contains(PossibleAction.PLAY);
     * 
     * @
     */
    public void playSecondCard(Player player, Card cards) {
        final var requester = Objects.requireNonNull(player, "Player must not be null!");
        final var requesterCard = Objects.requireNonNull(cards, "Card must not be null!");
        validateRequest(requester, PossibleAction.PLAY);
        eventPlayer = currentPlayer;
        state.playSecondCard(requester, requesterCard);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires requester != null;
     * 
     * @ requires requester.equals(currentPlayer);
     * 
     * @ requires possibleActions.contains(PossibleAction.RAISE);
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (NullPointerException e) requester == null;
     * 
     * @ signals (IllegalArgumentException e) !requester.equals(currentPlayer);
     * 
     * @ signals (IllegalStateException e)
     * !possibleActions.contains(PossibleAction.RAISE);
     * 
     * @
     */
    public void raise(Player requester) {
        final var player = Objects.requireNonNull(requester, "Player must not be null!");
        validateRequest(requester, PossibleAction.RAISE);
        eventPlayer = currentPlayer;
        state.raise(player);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires responder != null;
     * 
     * @ requires responder.equals(currentPlayer);
     * 
     * @ requires possibleActions.contains(PossibleAction.ACCEPT);
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (NullPointerException e) responder == null;
     * 
     * @ signals (IllegalArgumentException e) !responder.equals(currentPlayer);
     * 
     * @ signals (IllegalStateException e)
     * !possibleActions.contains(PossibleAction.ACCEPT);
     * 
     * @
     */
    public void accept(Player responder) {
        final var player = Objects.requireNonNull(responder, "Player must not be null!");
        validateRequest(player, PossibleAction.ACCEPT);
        eventPlayer = currentPlayer;
        state.accept(player);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires responder != null;
     * 
     * @ requires responder.equals(currentPlayer);
     * 
     * @ requires possibleActions.contains(PossibleAction.QUIT);
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (NullPointerException e) responder == null;
     * 
     * @ signals (IllegalArgumentException e) !responder.equals(currentPlayer);
     * 
     * @ signals (IllegalStateException e)
     * !possibleActions.contains(PossibleAction.QUIT);
     * 
     * @
     */
    public void quit(Player responder) {
        final var player = Objects.requireNonNull(responder, "Player must not be null!");
        validateRequest(player, PossibleAction.QUIT);
        eventPlayer = currentPlayer;
        state.quit(player);
    }

    /*
     * @ private exceptional_behavior
     * 
     * @ signals (IllegalArgumentException e) !requester.equals(currentPlayer);
     * 
     * @ signals (IllegalStateException e) !possibleActions.contains(action);
     * 
     * @
     */
    private void validateRequest(Player requester, PossibleAction action) {
        if (!requester.equals(currentPlayer))
            throw new IllegalArgumentException(requester + " can not " + action + " in " + currentPlayer + " turn.");
        if (!possibleActions.contains(action))
            throw new IllegalStateException("Can not " + action + ", but " + possibleActions + ".");
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires event != null;
     * 
     * @ ensures history.size() == \old(history.size()) + 1;
     * 
     * @
     */
    public void updateHistory(Event event) {
        history.add(Intel.ofHand(this, event));
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires lastCard != null;
     * 
     * @ ensures roundsPlayed.size() == \old(roundsPlayed.size()) + 1;
     * 
     * @
     */
    public void playRound(Card lastCard) {
        final var round = new Round(firstToPlay, cardToPlayAgainst, lastToPlay, lastCard, vira);
        round.play();
        roundsPlayed.add(round);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures currentPlayer != null;
     * 
     * @
     */
    public void defineRoundPlayingOrder() {
        final var lastRoundWinner = roundsPlayed.isEmpty() ? Optional.empty()
                : roundsPlayed.get(roundsPlayed.size() - 1).getWinner();
        lastRoundWinner.filter(lastToPlay::equals).ifPresent(unused -> changePlayingOrder());
        currentPlayer = firstToPlay;
    }

    /*
     * @ private normal_behavior
     * 
     * @ ensures firstToPlay == \old(lastToPlay);
     * 
     * @ ensures lastToPlay == \old(firstToPlay);
     * 
     * @
     */
    private void changePlayingOrder() {
        var referenceHolder = firstToPlay;
        firstToPlay = lastToPlay;
        lastToPlay = referenceHolder;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires card != null;
     * 
     * @ requires dealtCards.contains(card);
     * 
     * @ requires !openCards.contains(card) || card.equals(Card.closed());
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (GameRuleViolationException e) !dealtCards.contains(card) ||
     * (openCards.contains(card) && !card.equals(Card.closed()));
     * 
     * @
     */
    public void addOpenCard(Card card) {
        if (!dealtCards.contains(card) && !card.equals(Card.closed()))
            throw new GameRuleViolationException("Card has not been dealt in this hand.");
        if (openCards.contains(card) && !card.equals(Card.closed()))
            throw new GameRuleViolationException("Card " + card + " has already been played during hand.");
        openCards.add(card);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires roundsPlayed.size() >= 2;
     * 
     * @
     */
    public void checkForWinnerAfterSecondRound() {
        var firstRoundWinner = roundsPlayed.get(0).getWinner();
        var secondRoundWinner = roundsPlayed.get(1).getWinner();

        if (firstRoundWinner.isEmpty() && secondRoundWinner.isPresent())
            result = HandResult.of(secondRoundWinner.get(), points);
        else if (firstRoundWinner.isPresent() && secondRoundWinner.isEmpty())
            result = HandResult.of(firstRoundWinner.get(), points);
        else if (secondRoundWinner.isPresent() && secondRoundWinner.get().equals(firstRoundWinner.get()))
            result = HandResult.of(secondRoundWinner.get(), points);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires roundsPlayed.size() == 3;
     * 
     * @
     */
    public void checkForWinnerAfterThirdRound() {
        var firstRoundWinner = roundsPlayed.get(0).getWinner();
        var lastRoundWinner = roundsPlayed.get(2).getWinner();

        if (lastRoundWinner.isEmpty() && firstRoundWinner.isPresent())
            result = HandResult.of(firstRoundWinner.get(), points);
        else
            result = lastRoundWinner.map(player -> HandResult.of(player, points)).orElseGet(HandResult::ofDraw);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public /* @ pure @ */ Optional<Card> getCardToPlayAgainst() {
        return Optional.ofNullable(cardToPlayAgainst);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.cardToPlayAgainst == cardToPlayAgainst;
     * 
     * @
     */
    public void setCardToPlayAgainst(Card cardToPlayAgainst) {
        this.cardToPlayAgainst = cardToPlayAgainst;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == currentPlayer;
     * 
     * @
     */
    public /* @ pure @ */ Player getCurrentPlayer() {
        return currentPlayer;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == eventPlayer;
     * 
     * @
     */
    public /* @ pure @ */ Player getEventPlayer() {
        return eventPlayer;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.currentPlayer == currentPlayer;
     * 
     * @
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == (result != null);
     * 
     * @
     */
    public /* @ pure @ */ boolean hasWinner() {
        return result != null;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public /* @ pure @ */ Optional<HandResult> getResult() {
        return Optional.ofNullable(result);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == possibleActions;
     * 
     * @
     */
    public /* @ pure @ */ EnumSet<PossibleAction> getPossibleActions() {
        return possibleActions;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires actions != null;
     * 
     * @ ensures this.possibleActions.equals(actions);
     * 
     * @
     */
    public void setPossibleActions(EnumSet<PossibleAction> actions) {
        this.possibleActions = EnumSet.copyOf(actions);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires !history.isEmpty();
     * 
     * @ ensures \result == history.get(history.size() - 1);
     * 
     * @
     */
    public /* @ pure @ */ Intel getLastIntel() {
        return history.get(history.size() - 1);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires player != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public /* @ pure @ */ Player getOpponentOf(Player player) {
        return player.equals(firstToPlay) ? lastToPlay : firstToPlay;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public /* @ pure @ */ List<Round> getRoundsPlayed() {
        return new ArrayList<>(roundsPlayed);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == roundsPlayed.size();
     * 
     * @
     */
    public /* @ pure @ */ int numberOfRoundsPlayed() {
        return roundsPlayed.size();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == (state instanceof Done);
     * 
     * @
     */
    public /* @ pure @ */ boolean isDone() {
        return state instanceof Done;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.result == result;
     * 
     * @
     */
    public void setResult(HandResult result) {
        this.result = result;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.points == points;
     * 
     * @
     */
    public void setPoints(HandPoints points) {
        this.points = points;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == firstToPlay;
     * 
     * @
     */
    public /* @ pure @ */ Player getFirstToPlay() {
        return firstToPlay;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == lastToPlay;
     * 
     * @
     */
    public /* @ pure @ */ Player getLastToPlay() {
        return lastToPlay;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == points;
     * 
     * @
     */
    public /* @ pure @ */ HandPoints getPoints() {
        return points;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.lastBetRaiser == lastBetRaiser;
     * 
     * @
     */
    public void setLastBetRaiser(Player lastBetRaiser) {
        this.lastBetRaiser = lastBetRaiser;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == openCards;
     * 
     * @
     */
    public /* @ pure @ */ List<Card> getOpenCards() {
        return openCards;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public /* @ pure @ */ List<Card> getDealtCards() {
        return new ArrayList<>(dealtCards);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public /* @ pure @ */ List<Intel> getIntelHistory() {
        return List.copyOf(history);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == lastBetRaiser;
     * 
     * @
     */
    public /* @ pure @ */ Player getLastBetRaiser() {
        return lastBetRaiser;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == state;
     * 
     * @
     */
    public /* @ pure @ */ HandState getState() {
        return state;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures this.state == state;
     * 
     * @
     */
    public void setState(HandState state) {
        this.state = state;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == vira;
     * 
     * @
     */
    public /* @ pure @ */ Card getVira() {
        return vira;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == (firstToPlay.getScore() == 11 ^ lastToPlay.getScore() ==
     * 11);
     * 
     * @
     */
    public /* @ pure @ */ boolean isMaoDeOnze() {
        return firstToPlay.getScore() == 11 ^ lastToPlay.getScore() == 11;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures pointsProposal != null;
     * 
     * @
     */
    public void addPointsProposal() {
        pointsProposal = points.increase();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures pointsProposal == null;
     * 
     * @
     */
    public void removePointsProposal() {
        pointsProposal = null;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == pointsProposal;
     * 
     * @
     */
    public /* @ pure @ */ HandPoints getPointsProposal() {
        return pointsProposal;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == (isPlayerAllowedToRaise() && isAllowedToRaise() &&
     * isAllowedToReRaise());
     * 
     * @
     */
    public /* @ pure @ */ boolean canRaiseBet() {
        return isPlayerAllowedToRaise() && isAllowedToRaise() && isAllowedToReRaise();
    }

    /*
     * @ private normal_behavior
     * 
     * @ ensures \result == (currentPlayer != lastBetRaiser);
     * 
     * @
     */
    private /* @ pure @ */ boolean isPlayerAllowedToRaise() {
        return currentPlayer != lastBetRaiser;
    }

    /*
     * @ private normal_behavior
     * 
     * @ //
     * 
     * @
     */
    private /* @ pure @ */ boolean isAllowedToRaise() {
        return points.get() < 12 && points.increase().get() <= getMaxHandPoints()
                && firstToPlay.getScore() < 11 && lastToPlay.getScore() < 11;
    }

    /*
     * @ private normal_behavior
     * 
     * @ // 
     * 
     * @
     */
    private /* @ pure @ */ boolean isAllowedToReRaise() {
        return pointsProposal == null
                || pointsProposal.get() < 12 && pointsProposal.increase().get() <= getMaxHandPoints();
    }

    /*
     * @ private normal_behavior
     * 
     * @ ensures \result >= 0;
     * 
     * @
     */
    private /* @ pure @ */ int getMaxHandPoints() {
        final int firstToPlayScore = firstToPlay.getScore();
        final int lastToPlayScore = lastToPlay.getScore();
        final int pointsToLosingPlayerWin = Player.MAX_SCORE - Math.min(firstToPlayScore, lastToPlayScore);
        return pointsToLosingPlayerWin % 3 == 0 ? pointsToLosingPlayerWin
                : pointsToLosingPlayerWin + (3 - pointsToLosingPlayerWin % 3);
    }
}