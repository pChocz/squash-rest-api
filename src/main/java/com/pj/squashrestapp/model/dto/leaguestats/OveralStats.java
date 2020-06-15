package com.pj.squashrestapp.model.dto.leaguestats;

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

  public OveralStats(final List<PerSeasonStats> perSeasonStatsList) {
    this.seasons = perSeasonStatsList.size();
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
