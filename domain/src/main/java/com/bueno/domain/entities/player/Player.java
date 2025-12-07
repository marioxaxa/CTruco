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

package com.bueno.domain.entities.player;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.hand.HandPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Player {

    /* @ spec_public @ */
    public static final int MAX_SCORE = 12;
    /* @ spec_public nullable @ */
    private List<Card> cards;
    /* @ spec_public @ */
    private final String username;
    /* @ spec_public @ */
    private final UUID uuid;
    /* @ spec_public @ */
    private int score;
    /* @ spec_public @ */
    private boolean isBot;

    /*
     * @ public invariant username != null;
     * 
     * @ public invariant uuid != null;
     * 
     * @ public invariant score >= 0 && score <= MAX_SCORE;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires username != null;
     * 
     * @ requires uuid != null;
     * 
     * @ requires score >= 0 && score <= MAX_SCORE;
     * 
     * @ ensures this.cards == cards;
     * 
     * @ ensures this.username == username;
     * 
     * @ ensures this.uuid == uuid;
     * 
     * @ ensures this.score == score;
     * 
     * @ ensures this.isBot == isBot;
     * 
     * @
     */
    public Player(List<Card> cards, String username, UUID uuid, int score, boolean isBot) {
        this.cards = cards;
        this.username = username;
        this.uuid = uuid;
        this.score = score;
        this.isBot = isBot;
    }

    private Player(UUID uuid, String username) {
        this(null, username, uuid, 0, false);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires uuid != null;
     * 
     * @ requires username != null;
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result.getUuid().equals(uuid);
     * 
     * @ ensures \result.getUsername().equals(username);
     * 
     * @ ensures !\result.isBot();
     * 
     * @
     */
    public static Player of(UUID uuid, String username) {
        final Player player = new Player(uuid, username);
        player.isBot = false;
        return player;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires botName != null;
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result.isBot();
     * 
     * @ ensures \result.getUsername().equals(botName);
     * 
     * @
     */
    public static Player ofBot(String botName) {
        return ofBot(UUID.randomUUID(), botName);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires uuid != null;
     * 
     * @ requires botName != null;
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result.isBot();
     * 
     * @ ensures \result.getUsername().equals(botName);
     * 
     * @ ensures \result.getUuid().equals(uuid);
     * 
     * @
     */
    public static Player ofBot(UUID uuid, String botName) {
        final Player bot = new Player(uuid, botName);
        bot.isBot = true;
        return bot;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires card != null;
     * 
     * @ requires cards.contains(card);
     * 
     * @ ensures !cards.contains(card);
     * 
     * @ ensures \result == card;
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (IllegalArgumentException e) !cards.contains(card);
     * 
     * @
     */
    public final Card play(Card card) {
        Card cardToPlay = Objects.requireNonNull(card);
        if (doesNotOwn(cardToPlay))
            throw new IllegalArgumentException("User doesn't own card " + cardToPlay + " to play it.");
        getCards().remove(cardToPlay);
        return cardToPlay;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires card != null;
     * 
     * @ requires cards.contains(card);
     * 
     * @ ensures !cards.contains(card);
     * 
     * @ ensures \result != null;
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (IllegalArgumentException e) !cards.contains(card);
     * 
     * @
     */
    public final Card discard(Card card) {
        Card discard = Objects.requireNonNull(card);
        if (doesNotOwn(discard))
            throw new IllegalArgumentException("User doesn't own card " + card + " to discard it.");
        getCards().remove(discard);
        return Card.closed();
    }

    private boolean doesNotOwn(Card discard) {
        return !getCards().contains(discard);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires handPoints != null;
     * 
     * @ ensures score >= \old(score);
     * 
     * @ ensures score <= MAX_SCORE;
     * 
     * @
     */
    public final void addScore(HandPoints handPoints) {
        this.score = Math.min(MAX_SCORE, this.score + handPoints.get());
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires cards != null;
     * 
     * @ ensures this.cards != null;
     * 
     * @ ensures this.cards.size() == cards.size();
     * 
     * @
     */
    public final void setCards(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == cards;
     * 
     * @
     */
    public /* @ pure @ */ List<Card> getCards() {
        return cards;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == username;
     * 
     * @
     */
    public /* @ pure @ */ String getUsername() {
        return username;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == score;
     * 
     * @
     */
    public /* @ pure @ */ int getScore() {
        return score;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == uuid;
     * 
     * @
     */
    public /* @ pure @ */ UUID getUuid() {
        return uuid;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result == isBot;
     * 
     * @
     */
    public /* @ pure @ */ boolean isBot() {
        return isBot;
    }

    @Override
    public String toString() {
        return String.format("Player = %s (%s) has %d point(s)", username, uuid, score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Player player = (Player) o;
        return uuid.equals(player.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
