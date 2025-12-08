package com.bueno.domain.usecases.tournament.usecase;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayMatchInParallelUseCase {
    /* @ spec_public @ */
    private final PlayTournamentMatchesUseCase playTournamentMatchesUseCase;

    /* @ public invariant playTournamentMatchesUseCase != null; @ */

    /*
     * @ public normal_behavior
     * 
     * @ requires playTournamentMatchesUseCase != null;
     * 
     * @ ensures this.playTournamentMatchesUseCase == playTournamentMatchesUseCase;
     * 
     * @
     */
    public PlayMatchInParallelUseCase(PlayTournamentMatchesUseCase playTournamentMatchesUseCase) {
        this.playTournamentMatchesUseCase = playTournamentMatchesUseCase;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires tournamentUuid != null;
     * 
     * @ requires numberOfSimulations >= 0;
     * 
     * @ ensures true;
     * 
     * @
     */
    @Async("taskExecutor")
    public void execute(UUID tournamentUuid, int chosenMatchNumber, int numberOfSimulations) {
        playTournamentMatchesUseCase.playOne(tournamentUuid, chosenMatchNumber, numberOfSimulations);
        System.out.println("Refreshed BD");
    }

}
