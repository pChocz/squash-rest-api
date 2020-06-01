package com.pj.squashrestapp.service;

import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.SetDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Getter
public class OveralStats {

  private final int seasons;
  private final int rounds;
  private final List<PerSeasonStats> perSeasonStatsList;
  private int matches;
  private int sets;
  private int points;

  public OveralStats(final Multimap<Integer, MatchDto> matchesPerSeason) {
    final Set<Long> roundsIds = new HashSet<>();

    this.perSeasonStatsList = new ArrayList<>();
    for (final int seasonNumber : matchesPerSeason.keySet()) {
      int matches = 0;
      int regularSets = 0;
      int tieBreaks = 0;
      int points = 0;

      for (final MatchDto match : matchesPerSeason.get(seasonNumber)) {
        roundsIds.add(match.getRoundId());
        matches++;
        for (final SetDto set : match.getSets()) {
          points += set.getFirstPlayerScore();
          points += set.getSecondPlayerScore();
          if (!set.isEmpty()) {
            if (set.isTieBreak()) {
              tieBreaks++;
            } else {
              regularSets++;
            }
          }
        }
      }
      final int tieBreaksPercents = 100 * tieBreaks / matches;

      final PerSeasonStats perSeasonStats = new PerSeasonStats(seasonNumber, matches, regularSets, tieBreaks, tieBreaksPercents, points);
      this.perSeasonStatsList.add(perSeasonStats);
    }

    this.seasons = matchesPerSeason.keySet().size();
    this.rounds = roundsIds.size();
    this.matches = 0;
    this.sets = 0;
    this.points = 0;
    for (final PerSeasonStats perSeasonStats : perSeasonStatsList) {
      this.matches += perSeasonStats.getMatches();
      this.sets += perSeasonStats.getRegularSets();
      this.sets += perSeasonStats.getTieBreaks();
      this.points += perSeasonStats.getPoints();
    }

  }

}
