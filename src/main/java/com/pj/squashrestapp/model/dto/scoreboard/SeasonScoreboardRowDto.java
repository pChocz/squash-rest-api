package com.pj.squashrestapp.model.dto.scoreboard;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.dto.PlayerDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeasonScoreboardRowDto implements Comparable<SeasonScoreboardRowDto> {

  @EqualsAndHashCode.Include
  private final PlayerDto player;
  private final Map<Integer, Integer> roundNumberToXpMap;
  private final int bonusPoints;
  private int average;
  private int attendices;
  private int totalPoints;
  private int countedPoints;
  private int eightBestPoints;

  public SeasonScoreboardRowDto(final PlayerDto player, final AtomicLongMap<Long> bonusPointsAggregatedPerPlayer) {
    this.player = player;
    this.roundNumberToXpMap = new HashMap<>();

    final Long currentPlayerId = player.getId();
    this.bonusPoints = (int) bonusPointsAggregatedPerPlayer.get(currentPlayerId);
  }

  public void addXpForRound(final int roundNumber, final Integer xpEarned) {
    this.roundNumberToXpMap.put(roundNumber, xpEarned);
  }

  public void calculateFinishedRow(final int finishedRounds, final int countedRounds) {
    final int totalPointsForRounds = getTotalPointsForRounds();
    this.totalPoints = totalPointsForRounds + bonusPoints;

    final int numberOfRoundsThatCount = countedRounds;
    final int countedPointsForRounds = getPointsForNumberOfRounds(numberOfRoundsThatCount);
    final int eightBestPointsForRounds = getPointsForNumberOfRounds(8);
    this.countedPoints = countedPointsForRounds + bonusPoints;
    this.eightBestPoints = eightBestPointsForRounds + bonusPoints;

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

  private int getPointsForNumberOfRounds(final int numberOfRounds) {
    return roundNumberToXpMap
            .values()
            .stream()
            .sorted(Comparator.reverseOrder())
            .limit(numberOfRounds)
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
