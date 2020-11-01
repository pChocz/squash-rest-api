package com.pj.squashrestapp.model.dto.scoreboard;

import com.pj.squashrestapp.model.dto.PlayerDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

/**
 *
 *
 *
 */
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
    return Comparator
            .comparingInt(RoundGroupScoreboardRow::getMatchesBalance)
            .thenComparingInt(RoundGroupScoreboardRow::getSetsBalance)
            .thenComparingInt(RoundGroupScoreboardRow::getPointsBalance)
            .thenComparingInt(RoundGroupScoreboardRow::getSetsWon)
            .thenComparingInt(RoundGroupScoreboardRow::getPointsWon)
            .reversed()
            .compare(this, that);
  }

}
