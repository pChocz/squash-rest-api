package com.pj.squashrestapp.dto.matchresultsdistribution;

import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlayerMatchResultDistribution implements Comparable<PlayerMatchResultDistribution> {

    private PlayerDto player;
    private int matchesWon;
    private List<OpponentMatchResultDistribution> opponentMatchResultDistributionList;

    public PlayerMatchResultDistribution(final PlayerDto player,
                                         final List<OpponentMatchResultDistribution> opponentMatchResultDistributionList) {
        this.player = player;
        this.opponentMatchResultDistributionList = opponentMatchResultDistributionList;
    }

    public int getMatchesWon() {
        return opponentMatchResultDistributionList
                .stream()
                .mapToInt(OpponentMatchResultDistribution::getMatchesWon)
                .sum();
    }

    @Override
    public int compareTo(final PlayerMatchResultDistribution that) {
        return Comparator
                .comparingInt(PlayerMatchResultDistribution::getMatchesWon)
                .reversed()
                .compare(this, that);
    }

}
