package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.RomanUtil;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
public class SeasonDto implements Comparable<SeasonDto> {

  private final UUID leagueUuid;
  private final String leagueName;
  private final UUID seasonUuid;
  private final int seasonNumber;
  private final String seasonNumberRoman;
  private final String xpPointsType;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate seasonStartDate;

  public SeasonDto(final Season season) {
    this.leagueUuid = season.getLeague().getUuid();
    this.leagueName = season.getLeague().getName();
    this.seasonUuid = season.getUuid();
    this.seasonNumber = season.getNumber();
    this.seasonNumberRoman = RomanUtil.toRoman(season.getNumber());
    this.xpPointsType = season.getXpPointsType();
    this.seasonStartDate = season.getStartDate();
  }

  @Override
  public int compareTo(final SeasonDto that) {
    return Comparator.comparingInt(SeasonDto::getSeasonNumber).compare(this, that);
  }

  @Override
  public String toString() {
    return "S: " + seasonNumber + " | " + leagueName + " | uuid: " + seasonUuid;
  }
}
