package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.League;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LeagueDtoSimple {

  @EqualsAndHashCode.Include
  private final UUID leagueUuid;
  private final String leagueName;

  public LeagueDtoSimple(final League league) {
    this.leagueUuid = league.getUuid();
    this.leagueName = league.getName();
  }

  @Override
  public String toString() {
    return leagueName;
  }

}
