package com.bueno.domain.usecases.tournament.usecase;

import com.bueno.domain.usecases.tournament.dtos.MatchDTO;
import com.bueno.domain.usecases.tournament.repos.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaveMatchUseCase {
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
    public SaveMatchUseCase(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
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
    public void one(MatchDTO matchDTO) {
        matchRepository.save(matchDTO);
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires dtos != null;
     * 
     * @ ensures true;
     * 
     * @
     */
    public void all(List<MatchDTO> dtos) {
        matchRepository.saveAll(dtos);
    }
}
