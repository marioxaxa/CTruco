package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.usecases.tournament.dtos.MatchDTO;
import com.bueno.domain.usecases.tournament.repos.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UpdateMatchUseCase {
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
    public UpdateMatchUseCase(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires matchDTOS != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    public void updateAll(List<MatchDTO> matchDTOS) {
        matchRepository.updateAll(matchDTOS);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires matchDTO != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    public void update(MatchDTO matchDTO) {
        matchRepository.update(matchDTO);
    }
}
