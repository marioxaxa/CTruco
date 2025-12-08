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

import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.bot.providers.BotManagerService;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.game.converter.GameConverter;
import com.bueno.domain.usecases.game.dtos.CreateDetachedDto;
import com.bueno.domain.usecases.game.dtos.CreateForBotsDto;
import com.bueno.domain.usecases.game.dtos.CreateForUserAndBotDto;
import com.bueno.domain.usecases.game.repos.GameRepository;
import com.bueno.domain.usecases.intel.converters.IntelConverter;
import com.bueno.domain.usecases.intel.dtos.IntelDto;
import com.bueno.domain.usecases.user.UserRepository;
import com.bueno.domain.usecases.user.dtos.ApplicationUserDto;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import com.bueno.domain.usecases.utils.exceptions.IllegalGameEnrolmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class CreateGameUseCase {

    /* @ spec_public @ */
    private final GameRepository gameRepo;
    /* @ spec_public @ */
    private final UserRepository userRepo;
    /* @ spec_public @ */
    private final RemoteBotRepository botRepository;
    /* @ spec_public @ */
    private final RemoteBotApi remoteBotApi;
    /* @ spec_public @ */
    private final BotManagerService botManagerService;

    /*
     * @ public invariant gameRepo != null;
     * 
     * @ public invariant botRepository != null;
     * 
     * @ public invariant remoteBotApi != null;
     * 
     * @ public invariant botManagerService != null;
     * 
     * @
     */

    @Autowired
    /*
     * @ public normal_behavior
     * 
     * @ requires gameRepo != null;
     * 
     * @ requires botRepository != null;
     * 
     * @ requires remoteBotApi != null;
     * 
     * @ requires botManagerService != null;
     * 
     * @ ensures this.gameRepo == gameRepo;
     * 
     * @ ensures this.userRepo == userRepo;
     * 
     * @ ensures this.botRepository == botRepository;
     * 
     * @ ensures this.remoteBotApi == remoteBotApi;
     * 
     * @ ensures this.botManagerService == botManagerService;
     * 
     * @
     */
    public CreateGameUseCase(GameRepository gameRepo, UserRepository userRepo,
            RemoteBotRepository botRepository, RemoteBotApi remoteBotApi, BotManagerService botManagerService) {
        this.gameRepo = Objects.requireNonNull(gameRepo);
        this.userRepo = userRepo;
        this.botRepository = Objects.requireNonNull(botRepository);
        this.remoteBotApi = Objects.requireNonNull(remoteBotApi);
        this.botManagerService = botManagerService;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires gameRepo != null;
     * 
     * @ requires botRepository != null;
     * 
     * @ requires remoteBotApi != null;
     * 
     * @ requires botManagerService != null;
     * 
     * @ ensures this.gameRepo == gameRepo;
     * 
     * @ ensures this.userRepo == null;
     * 
     * @ ensures this.botRepository == botRepository;
     * 
     * @ ensures this.remoteBotApi == remoteBotApi;
     * 
     * @ ensures this.botManagerService == botManagerService;
     * 
     * @
     */
    public CreateGameUseCase(GameRepository gameRepo, RemoteBotRepository botRepository, RemoteBotApi remoteBotApi,
            BotManagerService botManagerService) {
        this(gameRepo, null, botRepository, remoteBotApi, botManagerService);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires request != null;
     * 
     * @ requires userRepo != null;
     * 
     * @ ensures \result != null;
     * 
     * @ signals (NullPointerException e) request == null;
     * 
     * @ signals (NoSuchElementException e) true;
     * 
     * @ signals (EntityNotFoundException e) true;
     * 
     * @ signals (IllegalGameEnrolmentException e) true;
     * 
     * @
     */
    public IntelDto createForUserAndBot(CreateForUserAndBotDto request) {
        Objects.requireNonNull(request, "Request not be null!");

        if (hasNoBotServiceWith(request.botName()))
            throw new NoSuchElementException("Service implementation not available: " + request.botName());

        final ApplicationUserDto user = userRepo.findByUuid(request.userUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found:" + request.userUuid()));

        final Player userPlayer = Player.of(user.uuid(), user.username());
        final Player botPlayer = Player.ofBot(request.botName());

        gameRepo.findByPlayerUuid(userPlayer.getUuid()).ifPresent(unused -> {
            throw new IllegalGameEnrolmentException(userPlayer.getUuid() + " is already playing a game.");
        });

        return create(userPlayer, botPlayer);
    }

    private boolean hasNoBotServiceWith(String botName) {
        List<String> availableBots = botManagerService.providersNames();
        boolean hasService = availableBots.contains(botName);
        return !Objects.requireNonNull(botManagerService.providersNames()).contains(botName);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires request != null;
     * 
     * @ ensures \result != null;
     * 
     * @ signals (NullPointerException e) request == null;
     * 
     * @ signals (NoSuchElementException e) true;
     * 
     * @
     */
    public IntelDto createDetached(CreateDetachedDto request) {
        Objects.requireNonNull(request, "Request model not be null!");

        if (hasNoBotServiceWith(request.botName()))
            throw new NoSuchElementException("Service implementation not available: " + request.botName());

        final Player userPlayer = Player.of(request.userUuid(), request.username());
        final Player botPlayer = Player.ofBot(request.botName());

        return create(userPlayer, botPlayer);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires request != null;
     * 
     * @ ensures \result != null;
     * 
     * @ signals (NullPointerException e) request == null;
     * 
     * @ signals (NoSuchElementException e) true;
     * 
     * @
     */
    public IntelDto createForBots(CreateForBotsDto request) {
        Objects.requireNonNull(request);

        if (hasNoBotServiceWith(request.bot1Name()))
            throw new NoSuchElementException("Service implementation not available: " + request.bot1Name());

        if (hasNoBotServiceWith(request.bot2Name()))
            throw new NoSuchElementException("Service implementation not available: " + request.bot2Name());

        final var bot1 = Player.ofBot(request.bot1Uuid(), request.bot1Name());
        final var bot2 = Player.ofBot(request.bot2Uuid(), request.bot2Name());

        return create(bot1, bot2);
    }

    private IntelDto create(Player p1, Player p2) {
        final Game game = new Game(p1, p2);
        gameRepo.save(GameConverter.toDto(game));
        return IntelConverter.toDto(game.getIntel());
    }
}
