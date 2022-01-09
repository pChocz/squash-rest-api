package com.pj.squashrestapp.dto.leaguestats;

import com.pj.squashrestapp.aspects.LoggableQuery;
import com.pj.squashrestapp.dto.scoreboard.EntireLeagueScoreboard;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeagueStatsWrapper implements LoggableQuery {

  private String leagueName;
  private UUID leagueUuid;
  private List<PerSeasonStats> perSeasonStats;
  private EntireLeagueScoreboard scoreboard;

  @Override
  public String message() {
    return toString();
  }

  @Override
  public String toString() {
    return "League Stats - " + leagueName;
  }
}
