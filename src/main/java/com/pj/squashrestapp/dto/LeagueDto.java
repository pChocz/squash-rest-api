package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.League;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Getter
public class LeagueDto {

  private final UUID leagueUuid;
  private final String leagueName;
  private final Set<SeasonDto> seasons;
  @Setter
  private byte[] leagueLogo;

  public LeagueDto(final League league) {
    this.leagueUuid = league.getUuid();
    this.leagueName = league.getName();

    this.seasons = league
            .getSeasons()
            .stream()
            .map(SeasonDto::new)
            .collect(Collectors.toCollection(TreeSet::new));
  }

  @Override
  public String toString() {
    return leagueName;
  }

}
