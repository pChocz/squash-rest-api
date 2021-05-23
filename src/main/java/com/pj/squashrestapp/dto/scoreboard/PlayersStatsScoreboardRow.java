package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import java.util.Comparator;
import lombok.Getter;
import lombok.Setter;

/** */
@Getter
@Setter
public class PlayersStatsScoreboardRow
    implements Comparable<PlayersStatsScoreboardRow>, ScoreboardRow {

  private final PlayerDto player;

  private int pointsWon;
  private int pointsLost;

  private int setsWon;
  private int setsLost;

  private int matchesWon;
  private int matchesLost;

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
