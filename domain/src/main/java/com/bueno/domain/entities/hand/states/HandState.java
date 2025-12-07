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
import com.bueno.domain.entities.player.Player;

public interface HandState {
    /*
     * @ public normal_behavior
     * 
     * @ requires player != null;
     * 
     * @ requires card != null;
     * 
     * @
     */
    void playFirstCard(Player player, Card card);

    /*
     * @ public normal_behavior
     * 
     * @ requires player != null;
     * 
     * @ requires card != null;
     * 
     * @
     */
    void playSecondCard(Player player, Card card);

    /*
     * @ public normal_behavior
     * 
     * @ requires responder != null;
     * 
     * @
     */
    void accept(Player responder);

    /*
     * @ public normal_behavior
     * 
     * @ requires responder != null;
     * 
     * @
     */
    void quit(Player responder);

    /*
     * @ public normal_behavior
     * 
     * @ requires requester != null;
     * 
     * @
     */
    void raise(Player requester);

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    default String className() {
        return getClass().getSimpleName().toUpperCase();
    }
}
