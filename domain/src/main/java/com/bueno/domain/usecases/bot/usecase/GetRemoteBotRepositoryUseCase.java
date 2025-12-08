package com.bueno.domain.usecases.bot.usecase;

import com.bueno.domain.usecases.bot.dtos.RemoteBotDto;
import com.bueno.domain.usecases.bot.dtos.RemoteBotResponseModel;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.user.UserRepository;
import com.bueno.domain.usecases.user.dtos.ApplicationUserDto;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class GetRemoteBotRepositoryUseCase {
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
    public GetRemoteBotRepositoryUseCase(RemoteBotRepository remoteBotRepository, UserRepository userRepository) {
        this.remoteBotRepository = remoteBotRepository;
        this.userRepository = userRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ ensures \result.stream().allMatch(Objects::nonNull);
     * 
     * @
     */
    public List<RemoteBotResponseModel> getAll() {
        List<RemoteBotDto> botDtoList = remoteBotRepository.findAll();
        return botDtoList.stream()
                .map(bot -> new RemoteBotResponseModel(bot.name(), getUserDtoByRemoteBotDto(bot).username(), bot.url(),
                        bot.port(), bot.repositoryUrl()))
                .toList();
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires botName != null;
     * 
     * @ ensures \result != null;
     * 
     * @ signals (NullPointerException e) botName == null;
     * 
     * @ signals (EntityNotFoundException e) true;
     * 
     * @
     */
    public RemoteBotResponseModel getByName(String botName) {
        Objects.requireNonNull(botName, "botName is null");

        RemoteBotDto dto = remoteBotRepository.findByName(botName)
                .orElseThrow(() -> new EntityNotFoundException(botName + " not found"));
        ApplicationUserDto userDto = getUserDtoByRemoteBotDto(dto);
        return new RemoteBotResponseModel(dto.name(), userDto.username(), dto.url(), dto.port(), dto.repositoryUrl());
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires userId != null;
     * 
     * @ ensures \result != null;
     * 
     * @ signals (NullPointerException e) userId == null;
     * 
     * @ signals (EntityNotFoundException e) true;
     * 
     * @
     */
    public List<RemoteBotResponseModel> getByUserId(UUID userId) {
        Objects.requireNonNull(userId, "userId is null");

        ApplicationUserDto userDto = userRepository.findByUuid(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<RemoteBotDto> bots = remoteBotRepository.findByUserId(userId);
        return bots.stream().filter(RemoteBotDto::authorized).map(bot -> new RemoteBotResponseModel(bot.name(),
                userDto.username(), bot.url(), bot.port(), bot.repositoryUrl())).toList();
    }

    private ApplicationUserDto getUserDtoByRemoteBotDto(RemoteBotDto dto) {
        return userRepository.findByUuid(dto.user())
                .orElseThrow(() -> new EntityNotFoundException("User not found, userId might be wrong"));
    }
}
