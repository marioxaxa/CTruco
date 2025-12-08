package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.usecases.tournament.dtos.TournamentDTO;
import com.bueno.domain.usecases.tournament.repos.TournamentRepository;
import com.bueno.domain.usecases.utils.exceptions.InvalidRequestException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SaveTournamentUseCase {
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
    public SaveTournamentUseCase(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires dto != null;
     * 
     * @ requires dto.uuid() != null;
     * 
     * @ requires dto.matchUUIDs() != null;
     * 
     * @ requires dto.participantsNames() != null;
     * 
     * @ ensures true;
     * 
     * @ also
     * 
     * @ public exceptional_behavior
     * 
     * @ signals (InvalidRequestException);
     * 
     * @
     */
    public void save(TournamentDTO dto) {
        try {
            Objects.requireNonNull(dto.uuid());
            Objects.requireNonNull(dto.matchUUIDs());
            Objects.requireNonNull(dto.participantsNames());
            tournamentRepository.save(dto);
        } catch (Exception e) {
            throw new InvalidRequestException("cannot save a invalid tournament dto");
        }
    }
}
