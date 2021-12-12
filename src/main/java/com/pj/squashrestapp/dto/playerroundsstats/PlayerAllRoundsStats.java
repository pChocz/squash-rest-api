package com.pj.squashrestapp.dto.playerroundsstats;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.scoreboard.PlayersStatsScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.ScoreboardRow;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class PlayerAllRoundsStats {

  ScoreboardRow scoreboardRow;
  List<PlayerSingleRoundStats> playerSingleRoundStats;

  public PlayerAllRoundsStats(final PlayerDto playerDto) {
    this.scoreboardRow = new PlayersStatsScoreboardRow(playerDto);
    this.playerSingleRoundStats = new ArrayList<>();
  }

  public void addSingleRoundStats(final PlayerSingleRoundStats playerSingleRoundStats) {
    this.playerSingleRoundStats.add(playerSingleRoundStats);
  }

  public void calculateScoreboard() {
    int matchesWon = 0;
    int matchesLost = 0;
    int setsWon = 0;
    int setsLost = 0;
    int pointsWon = 0;
    int pointsLost = 0;
    for (final PlayerSingleRoundStats playerSingleRoundStats : this.getPlayerSingleRoundStats()) {
      RoundGroupScoreboardRow row = playerSingleRoundStats.getRow();
      matchesWon += row.getMatchesWon();
      matchesLost += row.getMatchesLost();
      setsWon += row.getSetsWon();
      setsLost += row.getSetsLost();
      pointsWon += row.getPointsWon();
      pointsLost += row.getPointsLost();
    }
    this.scoreboardRow.setMatchesWon(matchesWon);
    this.scoreboardRow.setMatchesLost(matchesLost);
    this.scoreboardRow.setSetsWon(setsWon);
    this.scoreboardRow.setSetsLost(setsLost);
    this.scoreboardRow.setPointsWon(pointsWon);
    this.scoreboardRow.setPointsLost(pointsLost);
  }
}
