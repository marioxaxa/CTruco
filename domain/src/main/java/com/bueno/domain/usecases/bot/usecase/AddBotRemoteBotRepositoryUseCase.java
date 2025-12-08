package com.bueno.domain.usecases.bot.usecase;

import com.bueno.domain.usecases.bot.dtos.RemoteBotDto;
import com.bueno.domain.usecases.bot.dtos.RemoteBotRequestModel;
import com.bueno.domain.usecases.bot.dtos.RemoteBotResponseModel;
import com.bueno.domain.usecases.bot.dtos.TransientRemoteBotDto;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.user.UserRepository;
import com.bueno.domain.usecases.user.dtos.ApplicationUserDto;
import com.bueno.domain.usecases.utils.exceptions.EntityAlreadyExistsException;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import com.bueno.domain.usecases.utils.exceptions.InvalidRequestException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class AddBotRemoteBotRepositoryUseCase {
    /* @ spec_public @ */
    private final RemoteBotRepository remoteBotRepository;
    /* @ spec_public @ */
    private final UserRepository userRepository;

    /*
     * @ public invariant remoteBotRepository != null;
     * 
     * @ public invariant userRepository != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires remoteBotRepository != null;
     * 
     * @ requires userRepository != null;
     * 
     * @ ensures this.remoteBotRepository == remoteBotRepository;
     * 
     * @ ensures this.userRepository == userRepository;
     * 
     * @
     */
    public AddBotRemoteBotRepositoryUseCase(RemoteBotRepository remoteBotRepository, UserRepository userRepository) {
        this.remoteBotRepository = remoteBotRepository;
        this.userRepository = userRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires dtoRequest != null;
     * 
     * @ requires dtoRequest.name() != null && dtoRequest.name().trim().length() >=
     * 4;
     * 
     * @ requires dtoRequest.url() != null && dtoRequest.url().trim().length() >= 4;
     * 
     * @ requires dtoRequest.port() != null && dtoRequest.port().trim().length() ==
     * 4;
     * 
     * @ requires dtoRequest.repositoryUrl() != null &&
     * !dtoRequest.repositoryUrl().trim().isEmpty();
     * 
     * @ ensures \result != null;
     * 
     * @ signals (NullPointerException e) dtoRequest == null || dtoRequest.name() ==
     * null || dtoRequest.url() == null || dtoRequest.port() == null ||
     * dtoRequest.repositoryUrl() == null;
     * 
     * @ signals (InvalidRequestException e) true;
     * 
     * @ signals (EntityAlreadyExistsException e) true;
     * 
     * @ signals (EntityNotFoundException e) true;
     * 
     * @
     */
    public RemoteBotResponseModel addBot(RemoteBotRequestModel dtoRequest) {
        Objects.requireNonNull(dtoRequest, "request is null");
        Objects.requireNonNull(dtoRequest.name(), "name is null");
        Objects.requireNonNull(dtoRequest.url(), "url is null");
        Objects.requireNonNull(dtoRequest.port(), "port is null");
        Objects.requireNonNull(dtoRequest.repositoryUrl(), "repository url is null");

        if (dtoRequest.name().trim().length() < 4)
            throw new InvalidRequestException("invalid name");

        if (dtoRequest.url().trim().length() < 4)
            throw new InvalidRequestException("invalid url");

        if (dtoRequest.port().trim().length() != 4)
            throw new InvalidRequestException("invalid port");

        if (dtoRequest.repositoryUrl().trim().isEmpty())
            throw new InvalidRequestException("invalid repository url");

        TransientRemoteBotDto dto = new TransientRemoteBotDto(UUID.randomUUID(),
                dtoRequest.userId(),
                dtoRequest.name(),
                dtoRequest.url(),
                dtoRequest.port(),
                dtoRequest.repositoryUrl());

        if (remoteBotRepository.existByName(dto.name())) {
            throw new EntityAlreadyExistsException("Trying to add a bot with same name of one already registered");
        }
        ApplicationUserDto userDto = userRepository.findByUuid(dto.user())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        remoteBotRepository.save(dto);
        return new RemoteBotResponseModel(dto.name(), userDto.username(), dto.url(), dto.port(), dto.repositoryUrl());
    }
}
