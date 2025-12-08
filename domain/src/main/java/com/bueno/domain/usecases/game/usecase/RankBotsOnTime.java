package com.bueno.domain.usecases.game.usecase;

import com.bueno.domain.usecases.game.repos.RankBotsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RankBotsOnTime {

    /* @ spec_public @ */
    private final RankBotsUseCase rankBotsUseCase;
    /* @ spec_public @ */
    private final RankBotsRepository rankBotsRepository;

    /*
     * @ public invariant rankBotsUseCase != null;
     * 
     * @ public invariant rankBotsRepository != null;
     * 
     * @
     */

    /*
     * @ public normal_behavior
     * 
     * @ requires rankBotsUseCase != null;
     * 
     * @ requires rankBotsRepository != null;
     * 
     * @ ensures this.rankBotsUseCase == rankBotsUseCase;
     * 
     * @ ensures this.rankBotsRepository == rankBotsRepository;
     * 
     * @
     */
    public RankBotsOnTime(RankBotsUseCase rankBotsUseCase, RankBotsRepository rankBotsRepository) {
        this.rankBotsUseCase = rankBotsUseCase;
        this.rankBotsRepository = rankBotsRepository;
    }
    // TODO - descomentar quando estiver em uma versão estável
    // @Scheduled(fixedRate = 1_800_000)
    // public void updateRankTable() {
    // rankBotsUseCase.rankAll();
    // rankBotsRepository.refreshTable(rankBotsUseCase.getRank());
    // }
}
