package com.pj.squashrestapp.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Season;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.UUID;

/**
 *
 */
@Slf4j
@Getter
public class SeasonDto implements Comparable<SeasonDto> {

  private final Long leagueId;
  private final UUID leagueUuid;
  private final String leagueName;
  private final Long seasonId;
  private final UUID seasonUuid;
  private final int seasonNumber;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate seasonStartDate;

  public SeasonDto(final Season season) {
    this.leagueId = season.getLeague().getId();
    this.leagueUuid = season.getLeague().getUuid();
    this.leagueName = season.getLeague().getName();
    this.seasonId = season.getId();
    this.seasonUuid = season.getUuid();
    this.seasonNumber = season.getNumber();
    this.seasonStartDate = season.getStartDate();
  }

  @Override
  public int compareTo(final SeasonDto that) {
    return Comparator
            .comparingInt(SeasonDto::getSeasonNumber)
            .compare(this, that);
  }

}
