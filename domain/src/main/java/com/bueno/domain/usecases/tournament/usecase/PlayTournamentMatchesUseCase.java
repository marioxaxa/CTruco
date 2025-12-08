package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.entities.tournament.Match;
import com.bueno.domain.entities.tournament.Tournament;
import com.bueno.domain.usecases.bot.providers.BotManagerService;
import com.bueno.domain.usecases.bot.providers.RemoteBotApi;
import com.bueno.domain.usecases.bot.repository.RemoteBotRepository;
import com.bueno.domain.usecases.tournament.converter.MatchConverter;
import com.bueno.domain.usecases.tournament.converter.TournamentConverter;
import com.bueno.domain.usecases.tournament.dtos.MatchDTO;
import com.bueno.domain.usecases.tournament.dtos.TournamentDTO;
import com.bueno.domain.usecases.tournament.repos.TournamentRepository;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayTournamentMatchesUseCase {
    /* @ spec_public @ */
    private final TournamentRepository tournamentRepository;
    /* @ spec_public @ */
    private final RemoteBotRepository remoteBotRepository;
    /* @ spec_public @ */
    private final RemoteBotApi api;
    /* @ spec_public @ */
    private final BotManagerService provider;
    /* @ spec_public @ */
    private final GetMatchUseCase getMatchUseCase;
    /* @ spec_public @ */
    private final UpdateTournamentUseCase updateTournamentUseCase;
    /* @ spec_public @ */
    private final UpdateMatchUseCase updateMatchUseCase;
    /* @ spec_public @ */
    private final RefreshTournamentUseCase refreshUseCase;

    /*
     * @ public invariant tournamentRepository != null;
     * 
     * @ public invariant remoteBotRepository != null;
     * 
     * @ public invariant api != null;
     * 
     * @ public invariant provider != null;
     * 
     * @ public invariant getMatchUseCase != null;
     * 
     * @ public invariant updateTournamentUseCase != null;
     * 
     * @ public invariant updateMatchUseCase != null;
     * 
     * @ public invariant refreshUseCase != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires tournamentRepository != null;
     * 
     * @ requires remoteBotRepository != null;
     * 
     * @ requires api != null;
     * 
     * @ requires botManagerService != null;
     * 
     * @ requires getMatchUseCase != null;
     * 
     * @ requires updateTournamentUseCase != null;
     * 
     * @ requires updateMatchUseCase != null;
     * 
     * @ requires refreshUseCase != null;
     * 
     * @ ensures this.tournamentRepository == tournamentRepository;
     * 
     * @ ensures this.remoteBotRepository == remoteBotRepository;
     * 
     * @ ensures this.api == api;
     * 
     * @ ensures this.provider == botManagerService;
     * 
     * @ ensures this.getMatchUseCase == getMatchUseCase;
     * 
     * @ ensures this.updateTournamentUseCase == updateTournamentUseCase;
     * 
     * @ ensures this.updateMatchUseCase == updateMatchUseCase;
     * 
     * @ ensures this.refreshUseCase == refreshUseCase;
     * 
     * @
     */
    public PlayTournamentMatchesUseCase(TournamentRepository tournamentRepository,
            RemoteBotRepository remoteBotRepository,
            RemoteBotApi api,
            BotManagerService botManagerService,
            GetMatchUseCase getMatchUseCase,
            UpdateTournamentUseCase updateTournamentUseCase,
            UpdateMatchUseCase updateMatchUseCase,
            RefreshTournamentUseCase refreshUseCase) {
        this.tournamentRepository = tournamentRepository;
        this.remoteBotRepository = remoteBotRepository;
        this.api = api;
        this.provider = botManagerService;
        this.getMatchUseCase = getMatchUseCase;
        this.updateTournamentUseCase = updateTournamentUseCase;
        this.updateMatchUseCase = updateMatchUseCase;
        this.refreshUseCase = refreshUseCase;
    }

    // public TournamentDTO playAll(TournamentDTO dto) {
    // Tournament tournament = TournamentConverter.fromDTO(dto, getMatchUseCase);
    // tournament.playAllAvailable(api, remoteBotRepository, provider);
    // tournament.setAvailableMatches();
    // return TournamentConverter.toDTO(tournament);
    // }

    // TODO - colocar este método no findMatchUseCase
    // TODO - colocar este método no findMatchUseCase
    /*
     * @ public normal_behavior
     * 
     * @ requires dto != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public List<MatchDTO> getAllAvailableMatches(TournamentDTO dto) {
        Tournament tournament = TournamentConverter.fromDTO(dto, getMatchUseCase);
        tournament.setAvailableMatches();
        return tournament.getMatches().stream()
                .filter(Match::isAvailable)
                .map(MatchConverter::toDTO)
                .toList();
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires uuid != null;
     * 
     * @ requires numberOfSimulations >= 0;
     * 
     * @ ensures true;
     * 
     * @ also
     * 
     * @ public exceptional_behavior
     * 
     * @ requires uuid != null;
     * 
     * @ signals (EntityNotFoundException);
     * 
     * @
     */
    public void playOne(UUID uuid, int chosenMatchNumber, int numberOfSimulations) {
        Optional<TournamentDTO> dto = tournamentRepository.findTournamentById(uuid);

        if (dto.isEmpty())
            throw new EntityNotFoundException("tournament doesn't exist");

        Optional<MatchDTO> chosenMatch = getMatchUseCase.byTournamentUuid(uuid)
                .stream()
                .filter(matchDTO -> matchDTO.matchNumber() == chosenMatchNumber)
                .findFirst();

        if (chosenMatch.isEmpty())
            throw new EntityNotFoundException("the chosenMatch doesn't exist");

        UUID chosenMatchId = chosenMatch.get().uuid();

        Tournament tournament = playChosenMatch(dto.get(), chosenMatchId, numberOfSimulations);
        TournamentDTO updatedDto = TournamentConverter.toDTO(tournament);
        updateTournamentUseCase.updateFromDTO(updatedDto);
        updateMatchUseCase.updateAll(tournament.getMatches().stream().map(MatchConverter::toDTO).toList());
        if (updatedDto != null)
            refreshUseCase.refresh(updatedDto.uuid());
    }

    private Tournament playChosenMatch(TournamentDTO dto, UUID chosenMatchId, int numberOfSimulations) {
        Tournament tournament = TournamentConverter.fromDTO(dto, getMatchUseCase);
        tournament.playByMatchUuid(chosenMatchId, api, provider, remoteBotRepository, numberOfSimulations);
        return tournament;
    }

}
