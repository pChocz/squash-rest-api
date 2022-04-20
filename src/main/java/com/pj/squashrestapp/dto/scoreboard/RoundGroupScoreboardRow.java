package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

/** */
@Getter
@Setter
@NoArgsConstructor
public class RoundGroupScoreboardRow implements Comparable<RoundGroupScoreboardRow>, ScoreboardRow {

    private PlayerDto player;

    private int pointsWon;
    private int pointsLost;
    private int pointsBalance;

    private int setsWon;
    private int setsLost;
    private int setsBalance;

    private int matchesWon;
    private int matchesLost;
    private int matchesBalance;

    private Integer xpEarned;
    private Integer placeInRound;
    private Integer placeInGroup;

    public RoundGroupScoreboardRow(final PlayerDto player) {
        this.player = player;
    }

    public int getPointsBalance() {
        return getPointsWon() - getPointsLost();
    }

    public int getSetsBalance() {
        return getSetsWon() - getSetsLost();
    }

    public int getMatchesBalance() {
        return getMatchesWon() - getMatchesLost();
    }

    @Override
    public int compareTo(final RoundGroupScoreboardRow that) {
        return Comparator.comparingInt(RoundGroupScoreboardRow::getMatchesBalance)
                .thenComparingInt(RoundGroupScoreboardRow::getSetsBalance)
                .thenComparingInt(RoundGroupScoreboardRow::getPointsBalance)
                .thenComparingInt(RoundGroupScoreboardRow::getSetsWon)
                .thenComparingInt(RoundGroupScoreboardRow::getPointsWon)
                .reversed()
                .compare(this, that);
    }
}
