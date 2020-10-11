package com.pj.squashrestapp.model.dto.scoreboard;

import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.match.MatchDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public interface ScoreboardRow {

  PlayerDto getPlayer();

  int getPointsWon();

  int getPointsLost();

  void setPointsWon(int value);

  void setPointsLost(int value);

  int getSetsWon();

  int getSetsLost();

  void setSetsWon(int value);

  void setSetsLost(int value);

  int getMatchesWon();

  int getMatchesLost();

  void setMatchesWon(int value);

  void setMatchesLost(int value);

  default void applyMatch(final MatchDto match) {
    final List<Integer> pointsWonPerSet = extractPointsWon(match);
    final List<Integer> pointsLostPerSet = extractPointsLost(match);

    final int numberOfSets = match.getSets().size();
    int currentMatchSetsWon = 0;
    int currentMatchSetsLost = 0;

    for (int i = 0; i < numberOfSets; i++) {
      final Integer pointsWon = pointsWonPerSet.get(i);
      final Integer pointsLost = pointsLostPerSet.get(i);

      setPointsWon(getPointsWon() + pointsWon);
      setPointsLost(getPointsLost() + pointsLost);

      if (pointsWon > pointsLost) {
        currentMatchSetsWon++;
      } else if (pointsWon < pointsLost) {
        currentMatchSetsLost++;
      }
    }

    setSetsWon(getSetsWon() + currentMatchSetsWon);
    setSetsLost(getSetsLost() + currentMatchSetsLost);

    if (currentMatchSetsWon > currentMatchSetsLost) {
      setMatchesWon(getMatchesWon() + 1);
    } else if (currentMatchSetsWon < currentMatchSetsLost) {
      setMatchesLost(getMatchesLost() + 1);
    }

  }

  default List<Integer> extractPointsWon(final MatchDto match) {
    final List<Integer> list = match
            .getSets()
            .stream()
            .map(setDto -> getPlayer().equals(match.getFirstPlayer())
                    ? setDto.getFirstPlayerScoreNullSafe()
                    : setDto.getSecondPlayerScoreNullSafe())
            .collect(Collectors.toList());
    return list;
  }

  default List<Integer> extractPointsLost(final MatchDto match) {
    final List<Integer> list = match
            .getSets()
            .stream()
            .map(setDto -> getPlayer().equals(match.getFirstPlayer())
                    ? setDto.getSecondPlayerScoreNullSafe()
                    : setDto.getFirstPlayerScoreNullSafe())
            .collect(Collectors.toList());
    return list;
  }

  default int getPointsBalance() {
    return getPointsWon() - getPointsLost();
  }

  default int getSetsBalance() {
    return getSetsWon() - getSetsLost();
  }

  default int getMatchesBalance() {
    return getMatchesWon() - getMatchesLost();
  }

//  default int getPointsPlayed() {
//    return getPointsWon() + getPointsLost();
//  }
//
//  default int getSetsPlayed() {
//    return getSetsWon() + getSetsLost();
//  }
//
//  default int getMatchesPlayed() {
//    return getMatchesWon() + getMatchesLost();
//  }

}
