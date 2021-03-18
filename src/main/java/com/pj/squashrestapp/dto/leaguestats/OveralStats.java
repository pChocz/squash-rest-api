package com.pj.squashrestapp.dto.leaguestats;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.util.RoundingUtil;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Getter
public class OveralStats {

  private final int seasons;
  private final int players;
  private final BigDecimal averagePlayers;
  private int rounds;
  private int matches;
  private int sets;
  private int points;

  public OveralStats(final List<PerSeasonStats> perSeasonStatsList) {
    this.seasons = perSeasonStatsList.size();
    this.rounds = 0;
    this.matches = 0;
    this.sets = 0;
    this.points = 0;

    final Multimap<UUID, UUID> overalPlayersAttendicesMap = LinkedHashMultimap.create();
    for (final PerSeasonStats perSeasonStats : perSeasonStatsList) {
      this.rounds += perSeasonStats.getRounds();
      this.matches += perSeasonStats.getRegularMatches() + perSeasonStats.getTieBreakMatches();
      this.sets += perSeasonStats.getRegularMatches() * 2;
      this.sets += perSeasonStats.getTieBreakMatches() * 3;
      this.points += perSeasonStats.getPoints();
      overalPlayersAttendicesMap.putAll(perSeasonStats.getPlayersAttendicesMap());
    }
    this.players = overalPlayersAttendicesMap.keySet().size();

    final float averagePlayers = (float) overalPlayersAttendicesMap.size() / this.rounds;
    this.averagePlayers = RoundingUtil.round(averagePlayers, 1);
  }

}
