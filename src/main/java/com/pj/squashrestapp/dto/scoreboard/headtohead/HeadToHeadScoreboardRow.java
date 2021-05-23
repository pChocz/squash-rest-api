package com.pj.squashrestapp.dto.scoreboard.headtohead;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.scoreboard.PlayersStatsScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.ScoreboardRow;
import com.pj.squashrestapp.util.RoundingUtil;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

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

  private int regularMatchesWon;
  private int regularMatchesLost;
  private BigDecimal regularMatchesRatio;

  private int tieBreakMatchesWon;
  private int tieBreakMatchesLost;
  private BigDecimal tieBreakMatchesRatio;

  private int firstSetsWon;
  private int firstSetsLost;
  private BigDecimal firstSetsRatio;

  private int secondSetsWon;
  private int secondSetsLost;
  private BigDecimal secondSetsRatio;

  private int tieBreaksWon;
  private int tieBreaksLost;
  private BigDecimal tieBreaksRatio;

  public HeadToHeadScoreboardRow(final PlayersStatsScoreboardRow row,
                                 final Map<PlayerDto, Map<Integer, Integer>> splitPerSet,
                                 final Map<PlayerDto, Map<Integer, Integer>> splitPerMatch) {

    this.player = row.getPlayer();

    this.pointsWon = row.getPointsWon();
    this.pointsLost = row.getPointsLost();
    this.pointsRatio = RoundingUtil.round((float) 100 * pointsWon / (pointsWon + pointsLost), 1);

    this.setsWon = row.getSetsWon();
    this.setsLost = row.getSetsLost();
    this.setsRatio = RoundingUtil.round((float) 100 * setsWon / (setsWon + setsLost), 1);

    this.matchesWon = row.getMatchesWon();
    this.matchesLost = row.getMatchesLost();
    this.matchesRatio = RoundingUtil.round((float) 100 * matchesWon / (matchesWon + matchesLost), 1);

    final PlayerDto opponent = splitPerSet
            .keySet()
            .stream()
            .filter(playerDto -> !playerDto.equals(this.player))
            .findFirst()
            .orElseThrow();

    this.firstSetsWon = splitPerSet.get(player).getOrDefault(1, 0);
    this.firstSetsLost = splitPerSet.get(opponent).getOrDefault(1, 0);
    this.firstSetsRatio = RoundingUtil.round((float) 100 * firstSetsWon / (firstSetsWon + firstSetsLost), 1);

    this.secondSetsWon = splitPerSet.get(player).getOrDefault(2, 0);
    this.secondSetsLost = splitPerSet.get(opponent).getOrDefault(2, 0);
    this.secondSetsRatio = RoundingUtil.round((float) 100 * secondSetsWon / (secondSetsWon + secondSetsLost), 1);

    this.tieBreaksWon = splitPerSet.get(player).getOrDefault(3, 0);
    this.tieBreaksLost = splitPerSet.get(opponent).getOrDefault(3, 0);
    if (tieBreaksWon + tieBreaksLost > 0) {
      this.tieBreaksRatio = RoundingUtil.round((float) 100 * tieBreaksWon / (tieBreaksWon + tieBreaksLost), 1);
    }

    this.regularMatchesWon = splitPerMatch.get(player).getOrDefault(2, 0);
    this.regularMatchesLost = splitPerMatch.get(opponent).getOrDefault(2, 0);
    if (regularMatchesWon + regularMatchesLost > 0) {
      this.regularMatchesRatio = RoundingUtil.round((float) 100 * regularMatchesWon / (regularMatchesWon + regularMatchesLost), 1);
    }

    this.tieBreakMatchesWon = splitPerMatch.get(player).getOrDefault(3, 0);
    this.tieBreakMatchesLost = splitPerMatch.get(opponent).getOrDefault(3, 0);
    if (tieBreakMatchesWon + tieBreakMatchesLost > 0) {
      this.tieBreakMatchesRatio = RoundingUtil.round((float) 100 * tieBreakMatchesWon / (tieBreakMatchesWon + tieBreakMatchesLost), 1);
    }
  }

  @Override
  public String toString() {
    return player.getUsername();
  }

}
