package com.pj.squashrestapp.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.SetDto;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
@SuppressWarnings("FieldMayBeFinal")
public class OveralStats {

  private int seasons;
  private int rounds;
  private int matches;
  private int sets;
  private int points;

  @JsonIgnore
  private List<PerSeasonStats> perSeasonStatsList;

  public OveralStats(final League league) {
    this.perSeasonStatsList = new ArrayList<>();

    for (final Season season : league.getSeasons()) {
      final List<MatchDto> matchesForSeason = MatchExtractorUtil.extractAllMatches(season);

      int matches = 0;
      int regularSets = 0;
      int tieBreaks = 0;
      int points = 0;

      for (final MatchDto match : matchesForSeason) {
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
      final int rounds = season.getRounds().size();
      final PerSeasonStats perSeasonStats = new PerSeasonStats(season.getNumber(), rounds, matches, regularSets, tieBreaks, tieBreaksPercents, points);
      this.perSeasonStatsList.add(perSeasonStats);
    }

    this.seasons = league.getSeasons().size();
    this.rounds = 0;
    this.matches = 0;
    this.sets = 0;
    this.points = 0;
    for (final PerSeasonStats perSeasonStats : perSeasonStatsList) {
      this.rounds += perSeasonStats.getRounds();
      this.matches += perSeasonStats.getMatches();
      this.sets += perSeasonStats.getRegularSets();
      this.sets += perSeasonStats.getTieBreaks();
      this.points += perSeasonStats.getPoints();
    }

  }

}
