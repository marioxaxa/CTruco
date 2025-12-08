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

package com.bueno.spi.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * Represents the game intel from the current player point of view, including
 * its card, the vira card, etc.
 * All information that a player can know about the game is available through
 * getter methods.
 * Objects of this class are immutable and must be created using the
 * {@link StepBuilder} supporting class.
 */
public class GameIntel {

    public enum RoundResult {
        WON, DREW, LOST
    }

    /* @ spec_public @ */
    private final List<TrucoCard> cards;
    /* @ spec_public @ */
    private final List<TrucoCard> openCards;
    /* @ spec_public @ */
    private final TrucoCard vira;
    /* @ spec_public nullable @ */
    private final TrucoCard opponentCard;
    /* @ spec_public @ */
    private final List<RoundResult> roundResults;
    /* @ spec_public @ */
    private final int score;
    /* @ spec_public @ */
    private final int opponentScore;
    /* @ spec_public @ */
    private final int handPoints;

    /*
     * @ public invariant cards != null;
     * 
     * @ public invariant openCards != null;
     * 
     * @ public invariant vira != null;
     * 
     * @ public invariant roundResults != null;
     * 
     * @ public invariant score >= 0;
     * 
     * @ public invariant score <= 12;
     * 
     * @ public invariant opponentScore >= 0;
     * 
     * @ public invariant opponentScore <= 12;
     * 
     * @ public invariant handPoints >= 0;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires cards != null;
     * 
     * @ requires openCards != null;
     * 
     * @ requires vira != null;
     * 
     * @ requires roundResults != null;
     * 
     * @ requires score >= 0;
     * 
     * @ requires opponentScore >= 0;
     * 
     * @ requires handPoints >= 0;
     * 
     * @ ensures this.cards == cards;
     * 
     * @ ensures this.openCards == openCards;
     * 
     * @ ensures this.vira == vira;
     * 
     * @ ensures this.opponentCard == opponentCard;
     * 
     * @ ensures this.roundResults == roundResults;
     * 
     * @ ensures this.score == score;
     * 
     * @ ensures this.opponentScore == opponentScore;
     * 
     * @ ensures this.handPoints == handPoints;
     * 
     * @
     */
    private GameIntel(List<TrucoCard> cards, List<TrucoCard> openCards, TrucoCard vira, TrucoCard opponentCard,
            List<RoundResult> roundResults, int score, int opponentScore, int handPoints) {
        this.cards = cards;
        this.openCards = openCards;
        this.vira = vira;
        this.opponentCard = opponentCard;
        this.roundResults = roundResults;
        this.score = score;
        this.opponentScore = opponentScore;
        this.handPoints = handPoints;
    }

    /**
     * <p>
     * Returns the cards owned by the bot in the current hand.
     * </p>
     * 
     * @return an unmodifiable List of non-null TrucoCards or an empty {@code List}
     *         if the user has no cards left
     */
    /**
     * <p>
     * Returns the cards owned by the bot in the current hand.
     * </p>
     * 
     * @return an unmodifiable List of non-null TrucoCards or an empty {@code List}
     *         if the user has no cards left
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result == cards;
     * 
     * @
     */
    public List<TrucoCard> getCards() {
        return cards;
    }

    /**
     * <p>
     * Returns cards in the order they were played in the current hand, including
     * the vira as the first {@code List}
     * element.
     * </p>
     * 
     * @return an unmodifiable List of non-null {@link TrucoCard} sorted by the
     *         ascending order they
     *         were played in the current hand. The vira card is the element of
     *         index 0.
     */
    /**
     * <p>
     * Returns cards in the order they were played in the current hand, including
     * the vira as the first {@code List}
     * element.
     * </p>
     * 
     * @return an unmodifiable List of non-null {@link TrucoCard} sorted by the
     *         ascending order they
     *         were played in the current hand. The vira card is the element of
     *         index 0.
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result == openCards;
     * 
     * @
     */
    public List<TrucoCard> getOpenCards() {
        return openCards;
    }

    /**
     * <p>
     * Returns the vira card of the current hand
     * </p>
     * 
     * @return a non-null {@link TrucoCard} describing the vira card of the current
     *         hand
     */
    /**
     * <p>
     * Returns the vira card of the current hand
     * </p>
     * 
     * @return a non-null {@link TrucoCard} describing the vira card of the current
     *         hand
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result == vira;
     * 
     * @
     */
    public TrucoCard getVira() {
        return vira;
    }

    /**
     * <p>
     * Returns an {@code Optional<TrucoCard>} that may contain a {@link TrucoCard}
     * played by the opponent to start
     * the round, or empty if the current player is the one that should start the
     * round.
     * </p>
     * 
     * @return an {@code Optional<TrucoCard>} containing a {@link TrucoCard} played
     *         by opponent or
     *         {@code Optional.empty()} if nothing was played and the current player
     *         is the first to play in the round.
     */
    /**
     * <p>
     * Returns an {@code Optional<TrucoCard>} that may contain a {@link TrucoCard}
     * played by the opponent to start
     * the round, or empty if the current player is the one that should start the
     * round.
     * </p>
     * 
     * @return an {@code Optional<TrucoCard>} containing a {@link TrucoCard} played
     *         by opponent or
     *         {@code Optional.empty()} if nothing was played and the current player
     *         is the first to play in the round.
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public Optional<TrucoCard> getOpponentCard() {
        return Optional.ofNullable(opponentCard);
    }

    /**
     * <p>
     * Returns an {@code List} that contains the round results of the current hand
     * from the point of
     * view of the current player. The results are represented by the values of
     * {@link RoundResult} (WON, DREW, or LOST).
     * For example, if the current player lost the first round, won second and is
     * playing the third, the list size is 2,
     * the first element is LOST, and the second is WON.
     * </p>
     * 
     * @return an unmodifiable {@code List} of non-null {@link RoundResult} elements
     *         in the order the rounds
     *         were played in the current hand, or an empty list if no round was
     *         concluded yet.
     */
    /**
     * <p>
     * Returns an {@code List} that contains the round results of the current hand
     * from the point of
     * view of the current player. The results are represented by the values of
     * {@link RoundResult} (WON, DREW, or LOST).
     * For example, if the current player lost the first round, won second and is
     * playing the third, the list size is 2,
     * the first element is LOST, and the second is WON.
     * </p>
     * 
     * @return an unmodifiable {@code List} of non-null {@link RoundResult} elements
     *         in the order the rounds
     *         were played in the current hand, or an empty list if no round was
     *         concluded yet.
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result == roundResults;
     * 
     * @
     */
    public List<RoundResult> getRoundResults() {
        return roundResults;
    }

    /**
     * <p>
     * Returns the player game score.
     * </p>
     * 
     * @return a non-negative int representing the current player score
     */
    /**
     * <p>
     * Returns the player game score.
     * </p>
     * 
     * @return a non-negative int representing the current player score
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result >= 0;
     * 
     * @ ensures \result == score;
     * 
     * @
     */
    public int getScore() {
        return score;
    }

    /**
     * <p>
     * Returns the opponent game score.
     * </p>
     * 
     * @return a non-negative int representing the current opponent score
     */
    /**
     * <p>
     * Returns the opponent game score.
     * </p>
     * 
     * @return a non-negative int representing the current opponent score
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result >= 0;
     * 
     * @ ensures \result == opponentScore;
     * 
     * @
     */
    public int getOpponentScore() {
        return opponentScore;
    }

    /**
     * <p>
     * Returns the number of points in dispute in the current hand.
     * </p>
     * 
     * @return a non-negative int representing the number of points in dispute
     */
    /**
     * <p>
     * Returns the number of points in dispute in the current hand.
     * </p>
     * 
     * @return a non-negative int representing the number of points in dispute
     */
    /*
     * @ public normal_behavior
     * 
     * @ ensures \result >= 0;
     * 
     * @ ensures \result == handPoints;
     * 
     * @
     */
    public int getHandPoints() {
        return handPoints;
    }

    public interface GeneralIntel {
        BotIntel gameInfo(List<RoundResult> roundResults, List<TrucoCard> openCards, TrucoCard vira, int handPoints);
    }

    public interface BotIntel {
        OpponentIntel botInfo(List<TrucoCard> cards, int score);
    }

    public interface OpponentIntel {
        StepBuilder opponentScore(int opponentScore);
    }

    /**
     * <p>
     * A Builder pattern implementation that supports the creation of GameIntel
     * objects. To start the build, the
     * method {@link #with()} should be invoked. To conclude the build, the
     * {@link #build()} method should be invoked.
     * The method {@link #opponentCard(TrucoCard card)} is the only optional step of
     * building process.
     * </p>
     */
    public static final class StepBuilder implements GeneralIntel, BotIntel, OpponentIntel {
        /* @ spec_public @ */
        private List<TrucoCard> cards;
        /* @ spec_public @ */
        private List<TrucoCard> openCards;
        /* @ spec_public @ */
        private TrucoCard vira;
        /* @ spec_public nullable @ */
        private TrucoCard opponentCard;
        /* @ spec_public @ */
        private List<RoundResult> roundResults;
        /* @ spec_public @ */
        private int score;
        /* @ spec_public @ */
        private int opponentScore;
        /* @ spec_public @ */
        private int handPoints;

        private StepBuilder() {
        }

        /**
         * <p>
         * Starts the building process of a GameIntel object.
         * </p>
         */
        /**
         * <p>
         * Starts the building process of a GameIntel object.
         * </p>
         */
        /*
         * @ public normal_behavior
         * 
         * @ ensures \result != null;
         * 
         * @
         */
        public static GeneralIntel with() {
            return new StepBuilder();
        }

        @Override
        /*
         * @ public normal_behavior
         * 
         * @ requires roundResults != null;
         * 
         * @ requires openCards != null;
         * 
         * @ requires vira != null;
         * 
         * @ requires handPoints >= 0;
         * 
         * @ ensures this.roundResults.equals(roundResults);
         * 
         * @ ensures this.openCards.equals(openCards);
         * 
         * @ ensures this.vira == vira;
         * 
         * @ ensures this.handPoints == handPoints;
         * 
         * @ ensures \result == this;
         * 
         * @
         */
        public BotIntel gameInfo(List<RoundResult> roundResults, List<TrucoCard> openCards, TrucoCard vira,
                int handPoints) {
            this.roundResults = List.copyOf((roundResults));
            this.openCards = List.copyOf(openCards);
            this.vira = vira;
            this.handPoints = handPoints;
            return this;
        }

        @Override
        /*
         * @ public normal_behavior
         * 
         * @ requires cards != null;
         * 
         * @ requires score >= 0;
         * 
         * @ ensures this.cards == cards;
         * 
         * @ ensures this.score == score;
         * 
         * @ ensures \result == this;
         * 
         * @
         */
        public OpponentIntel botInfo(List<TrucoCard> cards, int score) {
            this.cards = cards;
            this.score = score;
            return this;
        }

        @Override
        /*
         * @ public normal_behavior
         * 
         * @ requires opponentScore >= 0;
         * 
         * @ ensures this.opponentScore == opponentScore;
         * 
         * @ ensures \result == this;
         * 
         * @
         */
        public StepBuilder opponentScore(int opponentScore) {
            this.opponentScore = opponentScore;
            return this;
        }

        /**
         * <p>
         * Optional step of the building process. If this method is invoked more than
         * once, only the last
         * value will be used.
         * </p>
         */
        /**
         * <p>
         * Optional step of the building process. If this method is invoked more than
         * once, only the last
         * value will be used.
         * </p>
         */
        /*
         * @ public normal_behavior
         * 
         * @ requires card != null;
         * 
         * @ ensures this.opponentCard == card;
         * 
         * @ ensures \result == this;
         * 
         * @
         */
        public StepBuilder opponentCard(TrucoCard card) {
            this.opponentCard = card;
            return this;
        }

        /**
         * <p>
         * Concludes the building process of a GameIntel object.
         * </p>
         */
        /**
         * <p>
         * Concludes the building process of a GameIntel object.
         * </p>
         */
        /*
         * @ public normal_behavior
         * 
         * @ requires cards != null;
         * 
         * @ requires openCards != null;
         * 
         * @ requires vira != null;
         * 
         * @ requires roundResults != null;
         * 
         * @ requires score >= 0;
         * 
         * @ requires opponentScore >= 0;
         * 
         * @ requires handPoints >= 0;
         * 
         * @ ensures \result.getCards() == cards;
         * 
         * @ ensures \result != null;
         * 
         * @
         */
        public GameIntel build() {
            return new GameIntel(cards, openCards, vira, opponentCard, roundResults, score, opponentScore, handPoints);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GameIntel gameIntel = (GameIntel) o;
        return score == gameIntel.score && opponentScore == gameIntel.opponentScore
                && handPoints == gameIntel.handPoints
                && cards.equals(gameIntel.cards) && openCards.equals(gameIntel.openCards) && vira.equals(gameIntel.vira)
                && Objects.equals(opponentCard, gameIntel.opponentCard) && roundResults.equals(gameIntel.roundResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cards, openCards, vira, opponentCard, roundResults, score, opponentScore, handPoints);
    }
}
