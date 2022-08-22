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
    private int matchesLost;
    private double matchesRatio;
    private Set<MatchResultCount> matchesResultCountList;

    public OpponentMatchResultDistribution(final PlayerDto opponent,
                                           final Set<MatchResultCount> matchesResultCountList) {
        this.opponent = opponent;
        this.matchesResultCountList = matchesResultCountList;
    }

    public int getMatchesWon() {
        return matchesResultCountList
                .stream()
                .mapToInt(MatchResultCount::getMatchesWon)
                .sum();
    }

    public int getMatchesLost() {
        return matchesResultCountList
                .stream()
                .mapToInt(MatchResultCount::getMatchesLost)
                .sum();
    }

    public double getMatchesRatio() {
        return (double) getMatchesWon() / (getMatchesWon() + getMatchesLost());
    }

}
