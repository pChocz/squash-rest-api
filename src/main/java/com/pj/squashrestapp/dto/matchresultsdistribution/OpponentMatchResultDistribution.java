package com.pj.squashrestapp.dto.matchresultsdistribution;

import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OpponentMatchResultDistribution {

    private PlayerDto opponent;
    private int matchesWon;
    private Set<MatchResultCount> matchResultCountList;

    public OpponentMatchResultDistribution(final PlayerDto opponent,
                                           final Set<MatchResultCount> matchResultCountList) {
        this.opponent = opponent;
        this.matchResultCountList = matchResultCountList;
    }

    public int getMatchesWon() {
        return matchResultCountList
                .stream()
                .mapToInt(MatchResultCount::getMatchesWon)
                .sum();
    }
}
