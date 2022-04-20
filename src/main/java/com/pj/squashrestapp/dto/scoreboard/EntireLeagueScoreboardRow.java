package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.PlayerLeagueXpOveral;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class EntireLeagueScoreboardRow {

    private PlayerDto player;

    private int xpTotal;
    private int xpCounted;
    private int average;
    private int attendices;

    private int pointsWon;
    private int pointsLost;
    private int pointsBalance;

    private int setsWon;
    private int setsLost;
    private int setsBalance;

    private int matchesWon;
    private int matchesLost;
    private int matchesBalance;

    public EntireLeagueScoreboardRow(
            final PlayerLeagueXpOveral playerLeagueXpOveral, final RoundGroupScoreboardRow scoreboardRowForPlayer) {
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
