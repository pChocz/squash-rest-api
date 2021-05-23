package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import java.util.Comparator;
import lombok.Getter;
import lombok.Setter;

/** */
@Getter
@Setter
public class RoundGroupScoreboardRow implements Comparable<RoundGroupScoreboardRow>, ScoreboardRow {

  private final PlayerDto player;

  private int pointsWon;
  private int pointsLost;

  private int setsWon;
  private int setsLost;

  private int matchesWon;
  private int matchesLost;

  private Integer xpEarned;
  private Integer placeInRound;
  private Integer placeInGroup;

  public RoundGroupScoreboardRow(final PlayerDto player) {
    this.player = player;
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
