package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Season;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 *
 */
@Slf4j
@Getter
public class SeasonDto {

  private final Long leagueId;
  private final String leagueName;
  private final Long seasonId;
  private final int seasonNumber;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final Date seasonStartDate;

  public SeasonDto(final Season season) {
    this.leagueId = season.getLeague().getId();
    this.leagueName = season.getLeague().getName();
    this.seasonId = season.getId();
    this.seasonNumber = season.getNumber();
    this.seasonStartDate = season.getStartDate();
  }

}
