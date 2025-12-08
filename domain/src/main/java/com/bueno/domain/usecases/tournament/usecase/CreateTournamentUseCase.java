package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.entities.tournament.Tournament;
import com.bueno.domain.usecases.tournament.converter.MatchConverter;
import com.bueno.domain.usecases.tournament.converter.TournamentConverter;
import com.bueno.domain.usecases.tournament.dtos.TournamentDTO;
import com.bueno.domain.usecases.utils.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CreateTournamentUseCase {

    /* @ spec_public @ */
    private final SaveTournamentUseCase saveTournamentUseCase;
    /* @ spec_public @ */
    private final SaveMatchUseCase saveMatchUseCase;

    /*
     * @ public invariant saveTournamentUseCase != null;
     * 
     * @ public invariant saveMatchUseCase != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires saveTournamentUseCase != null;
     * 
     * @ requires saveMatchUseCase != null;
     * 
     * @ ensures this.saveTournamentUseCase == saveTournamentUseCase;
     * 
     * @ ensures this.saveMatchUseCase == saveMatchUseCase;
     * 
     * @
     */
    public CreateTournamentUseCase(SaveTournamentUseCase saveTournamentUseCase, SaveMatchUseCase saveMatchUseCase) {
        this.saveTournamentUseCase = saveTournamentUseCase;
        this.saveMatchUseCase = saveMatchUseCase;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires bots != null;
     * 
     * @ requires size > 0;
     * 
     * @ ensures \result != null;
     * 
     * @
     */
    public TournamentDTO createTournament(List<String> bots, int size, int times, int finalAndThirdPlaceMatchesTimes) {
        List<String> participants = new ArrayList<>(bots);
        Collections.shuffle(participants);
        return create(participants, size, times, finalAndThirdPlaceMatchesTimes);
    }

    /*
     * @ private normal_behavior
     * 
     * @ requires participants != null;
     * 
     * @ requires size > 0;
     * 
     * @ ensures \result != null;
     * 
     * @ also
     * 
     * @ private exceptional_behavior
     * 
     * @ signals (EntityNotFoundException);
     * 
     * @
     */
    private TournamentDTO create(List<String> participants, int size, int times, int finalAndThirdPlaceMatchTimes) {
        Tournament tournament = new Tournament(participants, size, times, finalAndThirdPlaceMatchTimes);
        tournament.insertMatches();
        tournament.insertParticipants();
        tournament.setNextMatches();
        tournament.setAvailableOnes();
        TournamentDTO dto = TournamentConverter.toDTO(tournament);
        if (dto == null)
            throw new EntityNotFoundException("fail to convert to DTO");
        saveTournamentUseCase.save(dto);
        saveMatchUseCase.all(tournament.getMatches().stream().map(MatchConverter::toDTO).toList());
        return dto;
    }

}
