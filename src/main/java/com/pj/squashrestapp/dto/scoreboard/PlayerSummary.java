package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.aspects.LoggableQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSummary implements LoggableQuery {

  private ScoreboardRow scoreboardRow;
  private int leagues;
  private int seasons;
  private int rounds;

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
