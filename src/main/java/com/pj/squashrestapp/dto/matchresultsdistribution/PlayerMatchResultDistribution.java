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
    private int matchesLost;
    private double matchesRatio;
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

    public int getMatchesLost() {
        return opponentMatchResultDistributionList
                .stream()
                .mapToInt(OpponentMatchResultDistribution::getMatchesLost)
                .sum();
    }

    public double getMatchesRatio() {
        return (double) getMatchesWon() / (getMatchesWon() + getMatchesLost());
    }

    @Override
    public int compareTo(final PlayerMatchResultDistribution that) {
        return Comparator
                .comparing(v -> ((PlayerMatchResultDistribution)v).getPlayer().getUsername())
                .compare(this, that);
    }

}
