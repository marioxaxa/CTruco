package com.bueno.domain.usecases.game.usecase;

import com.bueno.domain.usecases.game.dtos.BotRankInfoDto;
import com.bueno.domain.usecases.game.repos.RankBotsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRankBotsUseCase {
    /* @ spec_public @ */
    private final RankBotsRepository rankBotsRepository;
    /* @ spec_public nullable @ */
    private List<BotRankInfoDto> rank;

    /* @ public invariant rankBotsRepository != null; @ */

    /*
     * @ public normal_behavior
     * 
     * @ requires rankBotsRepository != null;
     * 
     * @ ensures this.rankBotsRepository == rankBotsRepository;
     * 
     * @
     */
    public GetRankBotsUseCase(RankBotsRepository rankBotsRepository) {
        this.rankBotsRepository = rankBotsRepository;
    }

    /*
     * @ public normal_behavior
     * 
     * @ ensures \result != null;
     * 
     * @ ensures this.rank == \result;
     * 
     * @
     */
    public List<BotRankInfoDto> exec() {
        rank = rankBotsRepository.findAll();

        return rank;
    }

    /*
     * @ public normal_behavior
     * 
     * @ requires rank != null;
     * 
     * @ ensures \result == rank.size();
     * 
     * @ ensures \result >= 0;
     * 
     * @ also
     * 
     * @ public exceptional_behavior
     * 
     * @ requires rank == null;
     * 
     * @ signals (NullPointerException e) true;
     * 
     * @
     */
    public int getNumberOfBots() {
        return rank.size();
    }

}
