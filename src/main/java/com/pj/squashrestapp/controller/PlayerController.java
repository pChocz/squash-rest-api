package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;


  @GetMapping(value = "/me")
  @ResponseBody
  PlayerDetailedDto aboutMe() {
    final PlayerDetailedDto aboutMeInfo = playerService.getAboutMeInfo();
    return aboutMeInfo;
  }


  @PutMapping(value = "/{playerUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  PlayerDetailedDto assignLeagueRole(@PathVariable final UUID playerUuid,
                                     @RequestParam final UUID leagueUuid,
                                     @RequestParam final LeagueRole leagueRole) {

    final PlayerDetailedDto playerDetailedDto = playerService.assignLeagueRole(playerUuid, leagueUuid, leagueRole);
    return playerDetailedDto;
  }

}
