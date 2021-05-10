package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.util.RoundingUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *
 */
@Getter
@Setter
public class HeadToHeadScoreboardRow implements ScoreboardRow {

  private final PlayerDto player;

  private int pointsWon;
  private int pointsLost;
  private BigDecimal pointsRatio;

  private int setsWon;
  private int setsLost;
  private BigDecimal setsRatio;

  private int matchesWon;
  private int matchesLost;
  private BigDecimal matchesRatio;

  public HeadToHeadScoreboardRow(final PlayersStatsScoreboardRow row) {
    this.player = row.getPlayer();

    this.pointsWon = row.getPointsWon();
    this.pointsLost = row.getPointsLost();
    this.pointsRatio = RoundingUtil.round( (float) 100 * pointsWon / (pointsWon + pointsLost), 1);

    this.setsWon = row.getSetsWon();
    this.setsLost = row.getSetsLost();
    this.setsRatio = RoundingUtil.round( (float) 100 * setsWon / (setsWon + setsLost), 1);

    this.matchesWon = row.getMatchesWon();
    this.matchesLost = row.getMatchesLost();
    this.matchesRatio = RoundingUtil.round( (float) 100 * matchesWon / (matchesWon + matchesLost), 1);
  }

  @Override
  public String toString() {
    return player.getUsername();
  }

}
