package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.SetDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class PerSeasonStats {

  private int seasonNumber;
  private int matches;
  private int regularSets;
  private int tieBreaks;
  private int tieBreaksPercents;
  private int points;

}
