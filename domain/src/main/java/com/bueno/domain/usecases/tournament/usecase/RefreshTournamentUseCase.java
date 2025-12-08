package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.entities.tournament.Match;
import com.bueno.domain.entities.tournament.Tournament;
import com.bueno.domain.usecases.tournament.converter.MatchConverter;
import com.bueno.domain.usecases.tournament.converter.TournamentConverter;
import com.bueno.domain.usecases.tournament.dtos.TournamentDTO;
import com.bueno.domain.usecases.tournament.repos.TournamentRepository;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTournamentUseCase {
    /* @ spec_public @ */
    private final TournamentRepository tournamentRepository;
    /* @ spec_public @ */
    private final GetMatchUseCase getMatchUseCase;
    /* @ spec_public @ */
    private final UpdateTournamentUseCase updateTournamentUseCase;
    /* @ spec_public @ */
    private final UpdateMatchUseCase updateMatchUseCase;

    /*
     * @ public invariant tournamentRepository != null;
     * 
     * @ public invariant getMatchUseCase != null;
     * 
     * @ public invariant updateTournamentUseCase != null;
     * 
     * @ public invariant updateMatchUseCase != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires tournamentRepository != null;
     * 
     * @ requires getMatchUseCase != null;
     * 
     * @ requires updateTournamentUseCase != null;
     * 
     * @ requires updateMatchUseCase != null;
     * 
     * @ ensures this.tournamentRepository == tournamentRepository;
     * 
     * @ ensures this.getMatchUseCase == getMatchUseCase;
     * 
     * @ ensures this.updateTournamentUseCase == updateTournamentUseCase;
     * 
     * @ ensures this.updateMatchUseCase == updateMatchUseCase;
     * 
     * @
     */
    public RefreshTournamentUseCase(TournamentRepository tournamentRepository,
            GetMatchUseCase getMatchUseCase,
            UpdateTournamentUseCase updateTournamentUseCase, UpdateMatchUseCase updateMatchUseCase) {
        this.tournamentRepository = tournamentRepository;
        this.getMatchUseCase = getMatchUseCase;
        this.updateTournamentUseCase = updateTournamentUseCase;
        this.updateMatchUseCase = updateMatchUseCase;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires tournamentUuid != null;
     * 
     * @ ensures true;
     * 
     * @ also
     * 
     * @ public exceptional_behavior
     * 
     * @ requires tournamentUuid != null;
     * 
     * @ signals (EntityNotFoundException);
     * 
     * @
     */
    public void refresh(UUID tournamentUuid) {
        Optional<TournamentDTO> dto = tournamentRepository.findTournamentById(tournamentUuid);
        if (dto.isEmpty())
            throw new EntityNotFoundException("invalid tournament uuid");
        updateTournamentUseCase.updateFromDTO(refreshMatches(dto.get()));
    }

    /*
     * @ private normal_behavior
     * 
     * @ requires dto != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    private TournamentDTO refreshMatches(TournamentDTO dto) {
        Tournament tournament = TournamentConverter.fromDTO(dto, getMatchUseCase);
        Map<UUID, Match> cacheUpdatedMatches = new HashMap<>();
        tournament.refreshMatches(cacheUpdatedMatches);
        updateMatchUseCase.updateAll(cacheUpdatedMatches.values().stream().map(MatchConverter::toDTO).toList());

        return TournamentConverter.toDTO(tournament);
    }

}
