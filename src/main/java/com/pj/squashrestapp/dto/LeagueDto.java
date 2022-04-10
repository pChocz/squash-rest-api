package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.MatchFormatType;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
public class LeagueDto {

  private final UUID leagueUuid;
  private final Long leagueId;
  private final String leagueName;
  private final MatchFormatType matchFormatType;
  private final String location;
  private final String time;
  private final Set<SeasonDto> seasons;
  @Setter private byte[] leagueLogo;

  public LeagueDto(final League league) {
    this.leagueUuid = league.getUuid();
    this.leagueId = league.getId();
    this.leagueName = league.getName();
    this.matchFormatType = league.getMatchFormatType();
    this.location = league.getLocation();
    this.time = league.getTime();

    this.seasons =
        league.getSeasons().stream()
            .map(SeasonDto::new)
            .collect(Collectors.toCollection(TreeSet::new));
  }

  @Override
  public String toString() {
    return leagueName;
  }
}
