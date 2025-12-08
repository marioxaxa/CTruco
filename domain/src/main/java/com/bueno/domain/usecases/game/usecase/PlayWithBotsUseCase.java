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

package com.bueno.domain.usecases.game.usecase;

import com.bueno.domain.usecases.bot.providers.BotManagerService;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.game.dtos.PlayWithBotsDto;
import com.bueno.domain.usecases.game.dtos.PlayWithBotsResultsDto;
import com.bueno.domain.usecases.game.service.SimulationService;

import java.util.List;
import java.util.UUID;

public class PlayWithBotsUseCase {

    /* @ spec_public @ */
    private final RemoteBotRepository remoteBotRepository;
    /* @ spec_public @ */
    private final RemoteBotApi remoteBotApi;
    /* @ spec_public @ */
    private final BotManagerService botManagerService;

    /*
     * @ public invariant remoteBotRepository != null;
     * 
     * @ public invariant remoteBotApi != null;
     * 
     * @ public invariant botManagerService != null;
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
     * @ requires botManagerService != null;
     * 
     * @ ensures this.remoteBotRepository == remoteBotRepository;
     * 
     * @ ensures this.remoteBotApi == remoteBotApi;
     * 
     * @ ensures this.botManagerService == botManagerService;
     * 
     * @
     */
    public PlayWithBotsUseCase(RemoteBotRepository remoteBotRepository, RemoteBotApi remoteBotApi,
            BotManagerService botManagerService) {
        this.remoteBotRepository = remoteBotRepository;
        this.remoteBotApi = remoteBotApi;
        this.botManagerService = botManagerService;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires uuidBot1 != null;
     * 
     * @ requires bot1Name != null;
     * 
     * @ requires uuidBot2 != null;
     * 
     * @ requires bot2Name != null;
     * 
     * @ requires times > 0;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public PlayWithBotsResultsDto playWithBots(UUID uuidBot1, String bot1Name, UUID uuidBot2, String bot2Name,
            int times) {
        final long start = System.currentTimeMillis();
        final var simulator = new SimulationService(remoteBotRepository, remoteBotApi, botManagerService);
        final List<PlayWithBotsDto> results = simulator.runInParallel(uuidBot1, bot1Name, uuidBot2, bot2Name, times);
        final long end = System.currentTimeMillis();
        final PlayWithBotsResultsDto response = new PlayWithBotsResultsDto(results, (end - start), times);
        return response;
    }
}
