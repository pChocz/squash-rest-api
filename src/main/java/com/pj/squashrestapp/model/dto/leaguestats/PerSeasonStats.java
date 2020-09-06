package com.pj.squashrestapp.model.dto.leaguestats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 *
 */
@Getter
@Builder
public class PerSeasonStats {

  private final int seasonNumber;
  private final int rounds;
  private final int regularMatches;
  private final int tieBreakMatches;
  private final BigDecimal tieBreakMatchesPercents;
  private final int points;
  private final int players;
  private final BigDecimal playersAverage;

  @JsonIgnore
  private final Multimap<Long, Long> playersAttendicesMap;

}
