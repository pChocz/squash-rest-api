package com.pj.squashrestapp.model.dto.leaguestats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PerSeasonStats {

  private int seasonNumber;
  private int rounds;
  private int matches;
  private int regularSets;
  private int tieBreaks;
  private int tieBreaksPercents;
  private int points;

}
