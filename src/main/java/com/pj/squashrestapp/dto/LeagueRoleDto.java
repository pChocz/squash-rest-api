package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.enums.LeagueRole;
import com.pj.squashrestapp.model.RoleForLeague;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

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
