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

package com.bueno.domain.entities.hand.states;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.GameRuleViolationException;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.intel.Event;
import com.bueno.domain.entities.intel.PossibleAction;
import com.bueno.domain.entities.player.Player;

import java.util.EnumSet;

public class OneCard implements HandState {

    /*
     * @ public invariant context != null;
     * 
     * @
     */
    /* @ spec_public @ */
    private final Hand context;

    /*
     * @ public normal_behavior
     * 
     * @ requires context != null;
     * 
     * @ ensures this.context == context;
     * 
     * @
     */
    public OneCard(Hand context) {
        this.context = context;
        setPossibleHandActions();
    }

    private void setPossibleHandActions() {
        final EnumSet<PossibleAction> possibleActions = EnumSet.of(PossibleAction.PLAY);
        if (context.canRaiseBet())
            possibleActions.add(PossibleAction.RAISE);
        context.setPossibleActions(possibleActions);
    }

    /*
     * @ also
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (IllegalStateException e) true;
     * 
     * @
     */
    @Override
    public void playFirstCard(Player player, Card card) {
        throw new IllegalStateException("First card has already been played: " + context.getCardToPlayAgainst());
    }

    /*
     * @ also
     * 
     * @ public normal_behavior
     * 
     * @ requires !isThrowingClosedCardInFirstRound(card);
     * 
     * @ ensures context.getOpenCards().contains(card);
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (GameRuleViolationException e)
     * isThrowingClosedCardInFirstRound(card);
     * 
     * @
     */
    @Override
    public void playSecondCard(Player player, Card card) {
        if (isThrowingClosedCardInFirstRound(card))
            throw new GameRuleViolationException("Can not throw a closed card in first round");
        context.addOpenCard(card);
        context.playRound(card);
        switch (context.numberOfRoundsPlayed()) {
            case 1 -> handleFirstRoundPostConditions();
            case 2 -> handleSecondRoundPostConditions();
            case 3 -> handleThirdRoundPostConditions();
        }
        context.setCardToPlayAgainst(null);
        context.updateHistory(Event.PLAY);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires card != null;
     * 
     * @ ensures \result == (context.numberOfRoundsPlayed() == 0 &&
     * card.isClosed());
     * 
     * @
     */
    private boolean isThrowingClosedCardInFirstRound(Card card) {
        return context.numberOfRoundsPlayed() == 0 && card.isClosed();
    }

    private void handleFirstRoundPostConditions() {
        context.defineRoundPlayingOrder();
        context.setState(new NoCard(context));
    }

    private void handleSecondRoundPostConditions() {
        context.checkForWinnerAfterSecondRound();
        if (context.hasWinner())
            context.setState(new Done(context));
        else {
            context.defineRoundPlayingOrder();
            context.setCurrentPlayer(context.getFirstToPlay());
            context.setState(new NoCard(context));
        }
    }

    private void handleThirdRoundPostConditions() {
        context.checkForWinnerAfterThirdRound();
        context.setState(new Done(context));
    }

    /*
     * @ also
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (IllegalStateException e) true;
     * 
     * @
     */
    @Override
    public void accept(Player responder) {
        throw new IllegalStateException("No raising bet request to be accepted.");
    }

    /*
     * @ also
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (IllegalStateException e) true;
     * 
     * @
     */
    @Override
    public void quit(Player responder) {
        throw new IllegalStateException("No raising bet request to quit.");
    }

    /*
     * @ also
     * 
     * @ public normal_behavior
     * 
     * @ requires requester != null;
     * 
     * @
     */
    @Override
    public void raise(Player requester) {
        context.addPointsProposal();
        context.setLastBetRaiser(requester);
        context.setCurrentPlayer(context.getFirstToPlay());
        context.setState(new WaitingRaiseResponse(context));
        context.updateHistory(Event.RAISE);
    }
}
