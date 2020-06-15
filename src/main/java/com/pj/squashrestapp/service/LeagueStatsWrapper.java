package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.dto.Scoreboard;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
@AllArgsConstructor
public class LeagueStatsWrapper {

  private final String leagueName;
  private final String logo64encoded;
  private final OveralStats overalStats;
  private final List<PerSeasonStats> perSeasonStats;
  private final Scoreboard scoreboard;
  private final List<HallOfFameSeason> hallOfFame;

}
