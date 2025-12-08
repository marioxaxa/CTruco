package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.usecases.tournament.dtos.MatchDTO;
import com.bueno.domain.usecases.tournament.repos.MatchRepository;
import com.bueno.domain.usecases.tournament.repos.TournamentRepository;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetMatchUseCase {
    /* @ spec_public @ */
    private final MatchRepository matchRepository;

    /* @ public invariant matchRepository != null; @ */

    /*
     * @ public normal_behavior
     * 
     * @ requires matchRepository != null;
     * 
     * @ ensures this.matchRepository == matchRepository;
     * 
     * @
     */
    public GetMatchUseCase(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires uuid != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public Optional<MatchDTO> byUuid(UUID uuid) {
        return matchRepository.findById(uuid);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires uuid != null;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public List<MatchDTO> byTournamentUuid(UUID uuid) {
        return matchRepository.findMatchesByTournamentId(uuid);
    }
}
