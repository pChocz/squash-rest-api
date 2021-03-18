package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.RoleForLeague;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 *
 */
@Slf4j
@Getter
public class LeagueRoleDto {

  private final UUID leagueUuid;
  private final String leagueName;
  private final LeagueRole leagueRole;

  public LeagueRoleDto(final RoleForLeague role) {
    this.leagueUuid = role.getLeague().getUuid();
    this.leagueName = role.getLeague().getName();
    this.leagueRole = role.getLeagueRole();
  }

}
