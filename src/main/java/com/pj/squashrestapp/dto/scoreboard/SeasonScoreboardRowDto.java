package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.util.RoundingUtil;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeasonScoreboardRowDto implements Comparable<SeasonScoreboardRowDto> {

  @EqualsAndHashCode.Include private final PlayerDto player;
  private final Map<Integer, Integer> roundNumberToXpMapAll;
  private final Map<Integer, Integer> roundNumberToXpMapPretenders;
  private final int bonusPoints;
  private BigDecimal average;
  private int attendices;
  private int totalPoints;
  private int countedPoints;
  private int countedPointsPretenders;
  private int eightBestPoints;

  private int pointsWon;
  private int pointsLost;
  private int pointsBalance;

  private int setsWon;
  private int setsLost;
  private int setsBalance;

  private int matchesWon;
  private int matchesLost;
  private int matchesBalance;

  public SeasonScoreboardRowDto(
      final PlayerDto player, final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {
    this.player = player;
    this.roundNumberToXpMapAll = new HashMap<>();
    this.roundNumberToXpMapPretenders = new HashMap<>();

    final UUID currentPlayerUuid = player.getUuid();
    this.bonusPoints = bonusPointsAggregatedForSeason.forPlayer(currentPlayerUuid);
  }

  public static double getAverageAsDouble(final SeasonScoreboardRowDto seasonScoreboardRowDto) {
    return (double) seasonScoreboardRowDto.totalPoints / seasonScoreboardRowDto.attendices;
  }

  public void addXpForRound(final int roundNumber, final Integer xpEarned) {
    this.roundNumberToXpMapAll.put(roundNumber, xpEarned);
  }

  public void addXpForRoundPretendents(final int roundNumber, final Integer xpEarned) {
    this.roundNumberToXpMapPretenders.put(roundNumber, xpEarned);
  }

  public void calculateFinishedRow(final int countedRounds) {
    final int totalPointsForRounds = getTotalPointsForRounds(roundNumberToXpMapAll);
    this.totalPoints = totalPointsForRounds + bonusPoints;

    final int numberOfRoundsThatCount = countedRounds;
    final int countedPointsForRounds =
        getPointsForNumberOfRounds(roundNumberToXpMapAll, numberOfRoundsThatCount);
    this.countedPointsPretenders =
        getPointsForNumberOfRounds(roundNumberToXpMapPretenders, numberOfRoundsThatCount);

    final int eightBestPointsForRounds = getPointsForNumberOfRounds(roundNumberToXpMapAll, 8);
    this.countedPoints = countedPointsForRounds + bonusPoints;
    this.eightBestPoints = eightBestPointsForRounds + bonusPoints;

    this.attendices = roundNumberToXpMapAll.size();
    this.average = RoundingUtil.round((float) totalPoints / attendices, 1);
  }

  private int getTotalPointsForRounds(final Map<Integer, Integer> roundNumberToXpMap) {
    return roundNumberToXpMap.values().stream().mapToInt(points -> points).sum();
  }

  private int getPointsForNumberOfRounds(
      final Map<Integer, Integer> roundNumberToXpMap, final int numberOfRounds) {
    return roundNumberToXpMap.values().stream()
        .sorted(Comparator.reverseOrder())
        .limit(numberOfRounds)
        .mapToInt(points -> points)
        .sum();
  }

  @Override
  public int compareTo(final SeasonScoreboardRowDto that) {
    return Comparator.comparingInt(SeasonScoreboardRowDto::getCountedPoints)
        .thenComparingInt(SeasonScoreboardRowDto::getTotalPoints)
        .thenComparingDouble(SeasonScoreboardRowDto::getAverageAsDouble)
        .reversed()
        .compare(this, that);
  }

  public void addScoreboardRow(final RoundGroupScoreboardRow scoreboardRow) {
    this.pointsWon += scoreboardRow.getPointsWon();
    this.pointsLost += scoreboardRow.getPointsLost();
    this.pointsBalance += scoreboardRow.getPointsBalance();

    this.setsWon += scoreboardRow.getSetsWon();
    this.setsLost += scoreboardRow.getSetsLost();
    this.setsBalance += scoreboardRow.getSetsBalance();

    this.matchesWon += scoreboardRow.getMatchesWon();
    this.matchesLost += scoreboardRow.getMatchesLost();
    this.matchesBalance += scoreboardRow.getMatchesBalance();
  }
}
