package com.pj.squashrestapp.model.dto.scoreboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.SetDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 *
 */
@Getter
@JsonInclude(NON_NULL)
public class ScoreboardRow implements Comparable<ScoreboardRow> {

  private final PlayerDto player;

  private int pointsWon;
  private int pointsLost;
  private int pointsBalance;

  private int setsWon;
  private int setsLost;
  private int setsBalance;

  private int matchesWon;
  private int matchesLost;
  private int matchesBalance;

  @Setter
  private Integer xpEarned;
  @Setter
  private Integer placeInRound;
  @Setter
  private Integer placeInGroup;

  public ScoreboardRow(final PlayerDto player) {
    this.player = player;
  }

  public void applyMatch(final MatchDto match) {
    if (player.equals(match.getFirstPlayer())) {

      int currentMatchSetsWon = 0;
      int currentMatchSetsLost = 0;

      for (final SetDto set : match.getSets()) {
        final int currentSetPointsWon = set.getFirstPlayerScore();
        final int currentSetPointsLost = set.getSecondPlayerScore();
        if (currentSetPointsWon > currentSetPointsLost) {
          currentMatchSetsWon++;
        } else if (currentSetPointsWon < currentSetPointsLost) {
          currentMatchSetsLost++;
        }
        pointsWon += currentSetPointsWon;
        pointsLost += currentSetPointsLost;
      }

      if (currentMatchSetsWon > currentMatchSetsLost) {
        matchesWon++;
      } else if (currentMatchSetsWon < currentMatchSetsLost) {
        matchesLost++;
      }

      setsWon += currentMatchSetsWon;
      setsLost += currentMatchSetsLost;

    } else if (player.equals(match.getSecondPlayer())) {

      int currentMatchSetsWon = 0;
      int currentMatchSetsLost = 0;

      for (final SetDto set : match.getSets()) {
        final int currentSetPointsWon = set.getSecondPlayerScore();
        final int currentSetPointsLost = set.getFirstPlayerScore();
        if (currentSetPointsWon > currentSetPointsLost) {
          currentMatchSetsWon++;
        } else if (currentSetPointsWon < currentSetPointsLost) {
          currentMatchSetsLost++;
        }
        pointsWon += currentSetPointsWon;
        pointsLost += currentSetPointsLost;
      }

      if (currentMatchSetsWon > currentMatchSetsLost) {
        matchesWon++;
      } else if (currentMatchSetsWon < currentMatchSetsLost) {
        matchesLost++;
      }

      setsWon += currentMatchSetsWon;
      setsLost += currentMatchSetsLost;
    }

    pointsBalance = pointsWon - pointsLost;
    setsBalance = setsWon - setsLost;
    matchesBalance = matchesWon - matchesLost;
  }

  @Override
  public int compareTo(final ScoreboardRow that) {
    return Comparator
            .comparingInt(ScoreboardRow::getMatchesBalance)
            .thenComparingInt(ScoreboardRow::getMatchesWon)
            .thenComparingInt(ScoreboardRow::getSetsBalance)
            .thenComparingInt(ScoreboardRow::getSetsWon)
            .thenComparingInt(ScoreboardRow::getPointsBalance)
            .thenComparingInt(ScoreboardRow::getPointsWon)
            .reversed()
            .compare(this, that);
  }

}
