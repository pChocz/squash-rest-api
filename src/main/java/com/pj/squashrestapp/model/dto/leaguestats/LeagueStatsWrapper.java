package com.pj.squashrestapp.model.dto.leaguestats;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.dto.scoreboard.EntireLeagueScoreboard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
@Builder
@AllArgsConstructor
public class LeagueStatsWrapper {

  private final String leagueName;
  private final byte[] logoBytes;
  private final OveralStats overalStats;
  private final List<PerSeasonStats> perSeasonStats;
  private final EntireLeagueScoreboard scoreboard;
  private final List<HallOfFameSeason> hallOfFame;

}
