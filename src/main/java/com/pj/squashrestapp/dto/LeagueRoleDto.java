package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.RoleForLeague;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class LeagueRoleDto {

  private UUID leagueUuid;
  private String leagueName;
  private LeagueRole leagueRole;

  public LeagueRoleDto(final RoleForLeague role) {
    this.leagueUuid = role.getLeague().getUuid();
    this.leagueName = role.getLeague().getName();
    this.leagueRole = role.getLeagueRole();
  }
}
