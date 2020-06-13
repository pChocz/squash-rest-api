package com.pj.squashrestapp.model.dto;

import com.google.common.util.concurrent.AtomicLongMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeasonScoreboardRowDto implements Comparable<SeasonScoreboardRowDto> {

  @EqualsAndHashCode.Include
  PlayerDto player;

  Map<Integer, Integer> roundNumberToXpMap;

  int bonusPoints;
  int average;
  int attendices;
  int totalPoints;
  int countedPoints;

  public SeasonScoreboardRowDto(final PlayerDto player, final AtomicLongMap<Long> bonusPointsAggregatedPerPlayer) {
    this.player = player;
    this.roundNumberToXpMap = new HashMap<>();

    final Long currentPlayerId = player.getId();
    this.bonusPoints = (int) bonusPointsAggregatedPerPlayer.get(currentPlayerId);
  }

  public void addXpForRound(final int roundNumber, final Integer xpEarned) {
    this.roundNumberToXpMap.put(roundNumber, xpEarned);
  }

  public void calculateFinishedRow(final int finishedRounds) {
    final int totalPointsForRounds = getTotalPointsForRounds();
    this.totalPoints = totalPointsForRounds + bonusPoints;

    final int numberOfRoundsThatCount = getNumberOfRoundsThatCount(finishedRounds);
    final int countedPointsForRounds = getCountedPointsForRounds(numberOfRoundsThatCount);
    this.countedPoints = countedPointsForRounds + bonusPoints;

    this.attendices = roundNumberToXpMap.size();
    this.average = totalPoints / attendices;
  }

  private int getTotalPointsForRounds() {
    return roundNumberToXpMap
            .values()
            .stream()
            .mapToInt(points -> points)
            .sum();
  }

  private int getNumberOfRoundsThatCount(final int finishedRounds) {
    final int numberOfRoundsThatCount;
    if (finishedRounds <= 4) {
      numberOfRoundsThatCount = finishedRounds;
    } else if (finishedRounds <= 8) {
      numberOfRoundsThatCount = finishedRounds - 1;
    } else {
      numberOfRoundsThatCount = finishedRounds - 2;
    }
    return numberOfRoundsThatCount;
  }

  private int getCountedPointsForRounds(final int numberOfRoundsThatCount) {
    return roundNumberToXpMap
            .values()
            .stream()
            .sorted(Comparator.reverseOrder())
            .limit(numberOfRoundsThatCount)
            .mapToInt(points -> points)
            .sum();
  }

  @Override
  public int compareTo(final SeasonScoreboardRowDto that) {
    return Comparator
            .comparingInt(SeasonScoreboardRowDto::getCountedPoints)
            .thenComparingInt(SeasonScoreboardRowDto::getTotalPoints)
            .reversed()
            .compare(this, that);
  }

}
