package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.RoleForLeague;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Getter
public class LeagueRoleDto {

  private final Long leagueId;
  private final String leagueName;
  private final LeagueRole leagueRole;

  public LeagueRoleDto(final RoleForLeague role) {
    this.leagueId = role.getLeague().getId();
    this.leagueName = role.getLeague().getName();
    this.leagueRole = role.getLeagueRole();
  }

}
