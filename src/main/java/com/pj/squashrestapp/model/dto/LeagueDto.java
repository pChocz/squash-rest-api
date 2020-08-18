package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.League;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Getter
public class LeagueDto {

  private final Long leagueId;
  private final String leagueName;
  @Setter
  private byte[] leagueLogo;
  private final Set<SeasonDto> seasons;

  public LeagueDto(final League league) {
    this.leagueId = league.getId();
    this.leagueName = league.getName();

    this.seasons = league
            .getSeasons()
            .stream()
            .map(SeasonDto::new)
            .collect(Collectors.toCollection(TreeSet::new));
  }

}
