package com.pj.squashrestapp.dto.leaguestats;

import com.pj.squashrestapp.aspects.LoggableQuery;
import com.pj.squashrestapp.dto.scoreboard.EntireLeagueScoreboard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Getter
@Builder
@AllArgsConstructor
public class LeagueStatsWrapper implements LoggableQuery {

  private final String leagueName;
  private final UUID leagueUuid;
  private final byte[] logoBytes;
  private final OveralStats overalStats;
  private final List<PerSeasonStats> perSeasonStats;
  private final EntireLeagueScoreboard scoreboard;
  private final List<SeasonTrophies> seasonTrophies;

  @Override
  public String message() {
    return toString();
  }

  @Override
  public String toString() {
    return "League Stats: " + leagueName + " | " + leagueUuid;
  }

}
