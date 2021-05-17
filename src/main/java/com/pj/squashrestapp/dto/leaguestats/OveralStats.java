package com.pj.squashrestapp.dto.leaguestats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 */
@Builder
@Getter
@AllArgsConstructor
public class OveralStats {

  private final String leagueName;
  private final UUID leagueUuid;
  private final String time;
  private final String location;
  private final int seasons;
  private final int players;
  private final BigDecimal averagePlayersPerRound;
  private final BigDecimal averagePlayersPerGroup;
  private final BigDecimal averageGroupsPerRound;
  private final int rounds;
  private final int matches;
  private final int sets;
  private final int points;

}
