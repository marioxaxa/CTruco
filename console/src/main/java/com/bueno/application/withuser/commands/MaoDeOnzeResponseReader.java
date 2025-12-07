/*
 *  Copyright (C) 2021 Lucas B. R. de Oliveira - IFSP/SCL
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

package com.bueno.application.withuser.commands;

import com.bueno.application.withuser.PlayAgainstBots;
import com.bueno.application.utils.Command;

import java.util.Scanner;

public class MaoDeOnzeResponseReader implements Command<MaoDeOnzeResponseReader.MaoDeOnzeChoice> {

    /*
     * @ public invariant mainCli != null;
     * 
     * @
     */
    private final PlayAgainstBots mainCli;

    public enum MaoDeOnzeChoice {
        QUIT, ACCEPT
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires mainCli != null;
     * 
     * @ ensures this.mainCli == mainCli;
     * 
     * @
     */
    public MaoDeOnzeResponseReader(PlayAgainstBots mainCli) {
        this.mainCli = mainCli;
    }

    /*
     * @ also
     * 
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    @Override
    public MaoDeOnzeChoice execute() {
        var scanner = new Scanner(System.in);
        while (true) {
            mainCli.printGameIntel(3000);
            System.out.print("O jogo está em mão de onze. Você aceita [s, n]: ");
            final var choice = scanner.nextLine().toLowerCase();

            if (isValidChoice(choice, "s", "n")) {
                printErrorMessage("Valor inválido!");
                continue;
            }
            return choice.equalsIgnoreCase("s") ? MaoDeOnzeChoice.ACCEPT : MaoDeOnzeChoice.QUIT;
        }
    }
}
