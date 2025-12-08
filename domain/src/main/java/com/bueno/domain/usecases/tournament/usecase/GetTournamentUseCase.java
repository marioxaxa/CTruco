package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.usecases.tournament.dtos.TournamentDTO;
import com.bueno.domain.usecases.tournament.repos.TournamentRepository;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetTournamentUseCase {
    /* @ spec_public @ */
    private final TournamentRepository tournamentRepository;

    /* @ public invariant tournamentRepository != null; @ */

    /*
     * @ public normal_behavior
     * 
     * @ requires tournamentRepository != null;
     * 
     * @ ensures this.tournamentRepository == tournamentRepository;
     * 
     * @
     */
    public GetTournamentUseCase(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires uuid != null;
     * 
     * @ ensures \result != null;
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
    public TournamentDTO byUuid(UUID uuid) {
        Optional<TournamentDTO> dto = tournamentRepository.findTournamentById(uuid);
        if (dto.isEmpty())
            throw new EntityNotFoundException("invalid tournament uuid");
        return dto.get();
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public List<TournamentDTO> all() {
        return tournamentRepository.findAll();
    }
}
