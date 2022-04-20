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
public class PlayersStatsScoreboardRow implements Comparable<PlayersStatsScoreboardRow>, ScoreboardRow {

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

    public PlayersStatsScoreboardRow(final PlayerDto player) {
        this.player = player;
    }

    @Override
    public int compareTo(final PlayersStatsScoreboardRow that) {
        return Comparator.comparingDouble(PlayersStatsScoreboardRow::getMatchesRatio)
                .thenComparingDouble(PlayersStatsScoreboardRow::getSetsRatio)
                .thenComparingDouble(PlayersStatsScoreboardRow::getPointsRatio)
                .reversed()
                .compare(this, that);
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

    private double getMatchesRatio() {
        return (double) this.matchesWon / (this.matchesWon + this.matchesLost);
    }

    private double getSetsRatio() {
        return (double) this.setsWon / (this.setsWon + this.setsLost);
    }

    private double getPointsRatio() {
        return (double) this.pointsWon / (this.pointsWon + this.pointsLost);
    }

    @Override
    public String toString() {
        return player.getUsername();
    }
}
