package com.pj.squashrestapp.dto.leaguestats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

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
  private final Multimap<UUID, UUID> playersAttendicesMap;

}
