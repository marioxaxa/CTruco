package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.usecases.tournament.dtos.TournamentDTO;
import com.bueno.domain.usecases.tournament.repos.MatchRepository;
import com.bueno.domain.usecases.tournament.repos.TournamentRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateTournamentUseCase {
    /* @ spec_public @ */
    private final TournamentRepository tournamentRepository;
    /* @ spec_public @ */
    private final MatchRepository matchRepository;
    /* @ spec_public @ */
    private final UpdateMatchUseCase updateMatchUseCase;

    /*
     * @ public invariant tournamentRepository != null;
     * 
     * @ public invariant matchRepository != null;
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
     * @ requires matchRepository != null;
     * 
     * @ requires updateMatchUseCase != null;
     * 
     * @ ensures this.tournamentRepository == tournamentRepository;
     * 
     * @ ensures this.matchRepository == matchRepository;
     * 
     * @ ensures this.updateMatchUseCase == updateMatchUseCase;
     * 
     * @
     */
    public UpdateTournamentUseCase(TournamentRepository tournamentRepository, MatchRepository matchRepository,
            UpdateMatchUseCase updateMatchUseCase) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.updateMatchUseCase = updateMatchUseCase;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires dto != null;
     * 
     * @ requires dto.uuid() != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    public void updateFromDTO(TournamentDTO dto) {
        tournamentRepository.update(dto);
        updateMatchUseCase.updateAll(matchRepository.findMatchesByTournamentId(dto.uuid()));
    }
}
