package com.bueno.domain.usecases.bot.providers;

import com.bueno.domain.usecases.bot.dtos.RemoteBotDto;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.spi.service.BotServiceProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BotManagerService {

    /* @ spec_public @ */
    private static final int HEALTH_CHECK_PERIOD = 2;

    /* @ spec_public @ */
    private final RemoteBotRepository repository;
    /* @ spec_public @ */
    private final RemoteBotApi api;
    /* @ spec_public @ */
    private final List<BotServiceProvider> remoteBotsCache = new ArrayList<>();
    /* @ spec_public nullable @ */
    private Instant lastCheck;

    /*
     * @ public invariant repository != null;
     * 
     * @ public invariant api != null;
     * 
     * @ public invariant remoteBotsCache != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires repository != null;
     * 
     * @ requires api != null;
     * 
     * @ ensures this.repository == repository;
     * 
     * @ ensures this.api == api;
     * 
     * @ ensures this.remoteBotsCache.isEmpty();
     * 
     * @ ensures this.lastCheck == null;
     * 
     * @
     */
    public BotManagerService(RemoteBotRepository repository, RemoteBotApi api) {
        this.repository = repository;
        this.api = api;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires botServiceName != null;
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result.getName().equals(botServiceName);
     * 
     * @ signals (NoSuchElementException e) true;
     * 
     * @
     */
    public BotServiceProvider load(String botServiceName) {
        final Predicate<BotServiceProvider> hasName = botImpl -> botImpl.getName().equals(botServiceName);
        final Optional<BotServiceProvider> possibleBot = providers().filter(hasName).findAny();
        return possibleBot.orElseThrow(
                () -> new NoSuchElementException("Service implementation not available: " + botServiceName));
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public List<String> providersNames() {
        return providers().map(BotServiceProvider::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /*
     * @ private normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    private Stream<BotServiceProvider> providers() {
        Stream<BotServiceProvider> spiProviders = ServiceLoader.load(BotServiceProvider.class).stream()
                .map(ServiceLoader.Provider::get);

        List<BotServiceProvider> bots = new ArrayList<>(spiProviders.toList());

        if (lastCheck == null || Duration.between(lastCheck, Instant.now()).toMinutes() > HEALTH_CHECK_PERIOD) {
            remoteBotsCache.addAll(repository.findAll().stream()
                    .filter(this::isHealth)
                    .map(this::toBotServiceProvider)
                    .toList());
            lastCheck = Instant.now();
        }
        bots.addAll(remoteBotsCache);

        return bots.stream();
    }

    private boolean isHealth(RemoteBotDto dto) {
        return api.isHealthy(dto);
    }

    private BotServiceProvider toBotServiceProvider(RemoteBotDto dto) {
        return new RemoteBotServiceProvider(api, dto);
    }
}
