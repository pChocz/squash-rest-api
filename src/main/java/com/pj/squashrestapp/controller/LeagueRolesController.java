package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.service.LeagueRolesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** */
@Slf4j
@RestController
@RequestMapping("/league-roles")
@RequiredArgsConstructor
public class LeagueRolesController {

    private final LeagueRolesService leagueRolesService;

    @PutMapping(value = "/by-uuid/{leagueUuid}/{playerUuid}/{role}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(
            """
             isOwnerOfLeague(#leagueUuid)
              || (hasRoleForLeague(#leagueUuid, 'MODERATOR') && #role == 'PLAYER')
            """)
    void assignLeagueRoleByPlayerUuid(
            @PathVariable final UUID leagueUuid,
            @PathVariable final UUID playerUuid,
            @PathVariable final LeagueRole role) {
        leagueRolesService.assignRoleByPlayerUuid(leagueUuid, playerUuid, role);
    }

    @DeleteMapping(value = "/by-uuid/{leagueUuid}/{playerUuid}/{role}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(
            """
             isOwnerOfLeague(#leagueUuid)
              || (hasRoleForLeague(#leagueUuid, 'MODERATOR') && #role == 'PLAYER')
            """)
    void unassignLeagueRoleByPlayerUuid(
            @PathVariable final UUID leagueUuid,
            @PathVariable final UUID playerUuid,
            @PathVariable final LeagueRole role) {
        leagueRolesService.unassignRoleByPlayerUuid(leagueUuid, playerUuid, role);
    }

    @PutMapping(value = "/by-username/{leagueUuid}/{playerUsername}/{role}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(
            """
             isOwnerOfLeague(#leagueUuid)
              || (hasRoleForLeague(#leagueUuid, 'MODERATOR') && #role == 'PLAYER')
            """)
    void assignLeagueRoleByPlayerName(
            @PathVariable final UUID leagueUuid,
            @PathVariable final String playerUsername,
            @PathVariable final LeagueRole role) {
        leagueRolesService.assignRoleByPlayerUsername(leagueUuid, playerUsername, role);
    }

    @DeleteMapping(value = "/by-username/{leagueUuid}/{playerUsername}/{role}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(
            """
             isOwnerOfLeague(#leagueUuid)
              || (hasRoleForLeague(#leagueUuid, 'MODERATOR') && #role == 'PLAYER')
            """)
    void unassignLeagueRoleByPlayerName(
            @PathVariable final UUID leagueUuid,
            @PathVariable final String playerUsername,
            @PathVariable final LeagueRole role) {
        leagueRolesService.unassignRoleByPlayerUsername(leagueUuid, playerUsername, role);
    }

    @PutMapping(value = "/join/{leagueUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void joinLeagueAsPlayer(@PathVariable final UUID leagueUuid) {
        leagueRolesService.joinLeagueAsPlayer(leagueUuid);
    }

    @DeleteMapping(value = "/leave/{leagueUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void leaveLeague(@PathVariable final UUID leagueUuid) {
        leagueRolesService.leaveLeague(leagueUuid);
    }
}
