package com.pj.squashrestapp.model.dto.scoreboard;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.service.BonusPointsAggregatedForSeason;
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
  private final Map<Integer, Integer> roundNumberToXpMapAll;
  private final Map<Integer, Integer> roundNumberToXpMapPretenders;
  private final int bonusPoints;
  private int average;
  private int attendices;
  private int totalPoints;
  private int countedPoints;
  private int countedPointsPretenders;
  private int eightBestPoints;

  public SeasonScoreboardRowDto(final PlayerDto player, final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {
    this.player = player;
    this.roundNumberToXpMapAll = new HashMap<>();
    this.roundNumberToXpMapPretenders = new HashMap<>();

    final Long currentPlayerId = player.getId();
    this.bonusPoints = bonusPointsAggregatedForSeason.forPlayer(currentPlayerId);
  }

  public void addXpForRound(final int roundNumber, final Integer xpEarned) {
    this.roundNumberToXpMapAll.put(roundNumber, xpEarned);
  }

  public void addXpForRoundPretendents(final int roundNumber, final Integer xpEarned) {
    this.roundNumberToXpMapPretenders.put(roundNumber, xpEarned);
  }

  public void calculateFinishedRow(final int finishedRounds, final int countedRounds) {
    final int totalPointsForRounds = getTotalPointsForRounds(roundNumberToXpMapAll);
    this.totalPoints = totalPointsForRounds + bonusPoints;

    final int numberOfRoundsThatCount = countedRounds;
    final int countedPointsForRounds = getPointsForNumberOfRounds(roundNumberToXpMapAll, numberOfRoundsThatCount);
    this.countedPointsPretenders = getPointsForNumberOfRounds(roundNumberToXpMapPretenders, numberOfRoundsThatCount);

    final int eightBestPointsForRounds = getPointsForNumberOfRounds(roundNumberToXpMapAll, 8);
    this.countedPoints = countedPointsForRounds + bonusPoints;
    this.eightBestPoints = eightBestPointsForRounds + bonusPoints;

    this.attendices = roundNumberToXpMapAll.size();
    this.average = totalPoints / attendices;
  }

  private int getTotalPointsForRounds(final Map<Integer, Integer> roundNumberToXpMap) {
    return roundNumberToXpMap
            .values()
            .stream()
            .mapToInt(points -> points)
            .sum();
  }

  private int getPointsForNumberOfRounds(final Map<Integer, Integer> roundNumberToXpMap, final int numberOfRounds) {
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
            .thenComparingInt(SeasonScoreboardRowDto::getAverage)
            .reversed()
            .compare(this, that);
  }

}
