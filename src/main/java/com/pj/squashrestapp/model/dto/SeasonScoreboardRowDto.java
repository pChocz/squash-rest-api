package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.RoundResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SeasonScoreboardRowDto implements Comparable<SeasonScoreboardRowDto>{

  @EqualsAndHashCode.Include
  PlayerDto player;

  Map<Integer, Integer> roundNumberToXpMap;
  //  int bonusPoints;
  int average;
  int attendices;
  int totalPoints;
  int countedPoints;
//  int place;


  public SeasonScoreboardRowDto(final PlayerDto player) {
    this.player = player;
    this.roundNumberToXpMap = new HashMap<>();
    this.attendices = 0;
    this.totalPoints = 0;
    this.average = 0;
    this.countedPoints = 0;
  }

  public void addXpForRound(final int roundNumber, final Integer xpEarned) {
    this.roundNumberToXpMap.put(roundNumber, xpEarned);
  }

  public void calculateFinishedRow(final int allRounds, final int finishedRounds) {
    this.attendices = roundNumberToXpMap.size();

    this.totalPoints = roundNumberToXpMap
            .values()
            .stream()
            .mapToInt(points -> points)
            .sum();

    this.average = totalPoints / attendices;

    final int numberOfRoundsThatCount;
    if (finishedRounds <= 4) {
      numberOfRoundsThatCount = finishedRounds;
    } else if (finishedRounds <= 8) {
      numberOfRoundsThatCount = finishedRounds - 1;
    } else {
      numberOfRoundsThatCount = finishedRounds - 2;
    }

    this.countedPoints = roundNumberToXpMap
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
