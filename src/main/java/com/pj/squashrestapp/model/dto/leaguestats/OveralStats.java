package com.pj.squashrestapp.model.dto.leaguestats;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.List;

/**
 *
 */
@Getter
public class OveralStats {

  private final int seasons;
  private final int players;
  private final int allAttendices;
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

    final Multimap<Long, Long> overalPlayersAttendicesMap = LinkedHashMultimap.create();
    for (final PerSeasonStats perSeasonStats : perSeasonStatsList) {
      this.rounds += perSeasonStats.getRounds();
      this.matches += perSeasonStats.getMatches();
      this.sets += perSeasonStats.getRegularSets();
      this.sets += perSeasonStats.getTieBreaks();
      this.points += perSeasonStats.getPoints();
      overalPlayersAttendicesMap.putAll(perSeasonStats.getPlayersAttendicesMap());
    }
    this.players = overalPlayersAttendicesMap.keySet().size();
    this.allAttendices = overalPlayersAttendicesMap.size();
  }

}
