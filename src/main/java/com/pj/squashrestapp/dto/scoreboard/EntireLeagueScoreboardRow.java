package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.PlayerLeagueXpOveral;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
public class EntireLeagueScoreboardRow {

  private final PlayerDto player;

  private final int xpTotal;
  private final int xpCounted;
  private final int average;
  private final int attendices;

  private final int pointsWon;
  private final int pointsLost;
  private final int pointsBalance;

  private final int setsWon;
  private final int setsLost;
  private final int setsBalance;

  private final int matchesWon;
  private final int matchesLost;
  private final int matchesBalance;

  public EntireLeagueScoreboardRow(
      final PlayerLeagueXpOveral playerLeagueXpOveral,
      final RoundGroupScoreboardRow scoreboardRowForPlayer) {
    this.player = playerLeagueXpOveral.getPlayer();

    this.xpTotal = playerLeagueXpOveral.getTotalPoints();
    this.xpCounted = playerLeagueXpOveral.getCountedPoints();
    this.average = playerLeagueXpOveral.getAverage();
    this.attendices = playerLeagueXpOveral.getAttendices();

    this.pointsWon = scoreboardRowForPlayer.getPointsWon();
    this.pointsLost = scoreboardRowForPlayer.getPointsLost();
    this.pointsBalance = scoreboardRowForPlayer.getPointsBalance();

    this.setsWon = scoreboardRowForPlayer.getSetsWon();
    this.setsLost = scoreboardRowForPlayer.getSetsLost();
    this.setsBalance = scoreboardRowForPlayer.getSetsBalance();

    this.matchesWon = scoreboardRowForPlayer.getMatchesWon();
    this.matchesLost = scoreboardRowForPlayer.getMatchesLost();
    this.matchesBalance = scoreboardRowForPlayer.getMatchesBalance();
  }
}
