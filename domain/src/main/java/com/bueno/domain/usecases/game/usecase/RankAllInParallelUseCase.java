package com.bueno.domain.usecases.game.usecase;

import com.bueno.domain.usecases.game.dtos.BotRankInfoDto;
import com.bueno.domain.usecases.game.repos.RankBotsRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RankAllInParallelUseCase implements Runnable {
    /* @ spec_public @ */
    private final RankBotsUseCase rankBotsUseCase;
    /* @ spec_public @ */
    private List<BotRankInfoDto> rank;
    /* @ spec_public @ */
    private final RankBotsRepository rankBotsRepository;

    /*
     * @ public invariant rankBotsUseCase != null;
     * 
     * @ public invariant rankBotsRepository != null;
     * 
     * @ public invariant rank != null;
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
     * @ ensures this.rank != null && this.rank.isEmpty();
     * 
     * @
     */
    public RankAllInParallelUseCase(RankBotsUseCase rankBotsUseCase, RankBotsRepository rankBotsRepository) {
        this.rankBotsUseCase = rankBotsUseCase;
        this.rankBotsRepository = rankBotsRepository;
        this.rank = new ArrayList<>();
    }

    @Override
    /*
     * @ public normal_behavior
     * 
     * @ ensures true;
     * 
     * @
     */
    public void run() {
        rankBotsUseCase.rankAll();
        rank = rankBotsUseCase.getRank();
        System.out.println("Vai Atualizar no banco agora");
        rank.forEach(System.out::println);
        rankBotsRepository.refreshTable(rank);
    }

}
