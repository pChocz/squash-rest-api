package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.aspects.LoggableQuery;
import lombok.Value;

@Value
public class PlayerSummary implements LoggableQuery {

  ScoreboardRow scoreboardRow;
  int leagues;
  int seasons;
  int rounds;

  @Override
  public String toString() {
    return scoreboardRow.getPlayer()
        + " | leagues: "
        + leagues
        + " | seasons: "
        + seasons
        + " | rounds: "
        + rounds;
  }

  @Override
  public String message() {
    return "Dashboard";
  }
}
