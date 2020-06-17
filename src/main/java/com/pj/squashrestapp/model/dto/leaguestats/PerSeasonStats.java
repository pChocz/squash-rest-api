package com.pj.squashrestapp.model.dto.leaguestats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 *
 */
@Getter
@Builder
public class PerSeasonStats {

  private final int seasonNumber;
  private final int rounds;
  private final int matches;
  private final int regularSets;
  private final int tieBreaks;
  private final int tieBreaksPercents;
  private final int points;
  private final int players;
  private final int allAttendices;

  @JsonIgnore
  private final Multimap<Long, Long> playersAttendicesMap;

}
