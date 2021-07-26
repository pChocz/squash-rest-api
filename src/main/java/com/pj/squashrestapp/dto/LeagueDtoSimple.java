package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LeagueDtoSimple {

  @EqualsAndHashCode.Include private final UUID leagueUuid;

  private final String leagueName;

  @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
  private final LocalDateTime dateOfCreation;

  public LeagueDtoSimple(final League league) {
    this.leagueUuid = league.getUuid();
    this.leagueName = league.getName();
    this.dateOfCreation = league.getDateOfCreation();
  }

  @Override
  public String toString() {
    return leagueName;
  }
}
