package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.service.LeagueRolesService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/league-roles")
@RequiredArgsConstructor
public class LeagueRolesController {

  private final LeagueRolesService leagueRolesService;

  @PutMapping(value = "/{leagueUuid}/{playerUuid}/{role}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void assignRole(
      @PathVariable final UUID leagueUuid,
      @PathVariable final UUID playerUuid,
      @PathVariable final LeagueRole role) {
    leagueRolesService.assignRole(leagueUuid, playerUuid, role);
  }

  @DeleteMapping(value = "/{leagueUuid}/{playerUuid}/{role}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void unassignRole(
      @PathVariable final UUID leagueUuid,
      @PathVariable final UUID playerUuid,
      @PathVariable final LeagueRole role) {
    leagueRolesService.unassignRole(leagueUuid, playerUuid, role);
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
