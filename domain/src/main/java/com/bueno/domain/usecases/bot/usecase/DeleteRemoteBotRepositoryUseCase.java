package com.bueno.domain.usecases.bot.usecase;

import com.bueno.domain.usecases.bot.dtos.RemoteBotDto;
import com.bueno.domain.usecases.bot.dtos.TransientRemoteBotDto;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class DeleteRemoteBotRepositoryUseCase {
    /* @ spec_public @ */
    private final RemoteBotRepository remoteBotRepository;

    /*
     * @ public invariant remoteBotRepository != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires remoteBotRepository != null;
     * 
     * @ ensures this.remoteBotRepository == remoteBotRepository;
     * 
     * @
     */
    public DeleteRemoteBotRepositoryUseCase(RemoteBotRepository remoteBotRepository) {
        this.remoteBotRepository = remoteBotRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires botName != null;
     * 
     * @ signals (NullPointerException e) botName == null;
     * 
     * @ signals (EntityNotFoundException e) true;
     * 
     * @
     */
    public void delete(String botName) {
        Objects.requireNonNull(botName, "botName is null");
        Optional<TransientRemoteBotDto> botToDelete = remoteBotRepository.findAll().stream()
                .filter(remoteBotDto -> remoteBotDto.name().equals(botName))
                .map(remoteBotDto -> new TransientRemoteBotDto(remoteBotDto.uuid(),
                        remoteBotDto.user(),
                        remoteBotDto.name(),
                        remoteBotDto.url(),
                        remoteBotDto.port(),
                        remoteBotDto.repositoryUrl()))
                .findFirst();
        if (botToDelete.isEmpty())
            throw new EntityNotFoundException("Bot with name: " + botName + " wasn't found");
        remoteBotRepository.delete(botToDelete.get());
    }
}
