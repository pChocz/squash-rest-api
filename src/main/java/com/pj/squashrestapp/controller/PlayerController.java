package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.service.PlayerService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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


  @GetMapping(value = "/all")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  List<PlayerDetailedDto> extractAllPlayers() {
    final List<PlayerDetailedDto> allPlayers = playerService.getAllPlayers();
    return allPlayers;
  }

  @GetMapping(value = "/me")
  @ResponseBody
  PlayerDetailedDto aboutMe() {
    final PlayerDetailedDto aboutMeInfo = playerService.getAboutMeInfo();
    return aboutMeInfo;
  }


  @PutMapping(value = "/role-unassign/{playerUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  PlayerDetailedDto unassignLeagueRole(@PathVariable final UUID playerUuid,
                                       @RequestParam final UUID leagueUuid,
                                       @RequestParam final LeagueRole leagueRole) {

    final PlayerDetailedDto playerDetailedDto = playerService.unassignLeagueRole(playerUuid, leagueUuid, leagueRole);
    return playerDetailedDto;
  }


  @PutMapping(value = "/role-assign/{playerUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  PlayerDetailedDto assignLeagueRole(@PathVariable final UUID playerUuid,
                                     @RequestParam final UUID leagueUuid,
                                     @RequestParam final LeagueRole leagueRole) {

    final PlayerDetailedDto playerDetailedDto = playerService.assignLeagueRole(playerUuid, leagueUuid, leagueRole);
    return playerDetailedDto;
  }


  @PostMapping(value = "/newEnabled")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  PlayerDetailedDto createNewPlayerEnabled(@RequestParam final String username,
                                           @RequestParam final String email,
                                           @RequestParam final String password) {

    final String correctlyCapitalizedUsername = GeneralUtil.buildProperUsername(username);
    final String lowerCaseEmailAdress = email.toLowerCase();

    final boolean isValid = playerService.isValidSignupData(correctlyCapitalizedUsername, lowerCaseEmailAdress, password);
    if (isValid) {
      final Player newPlayer = playerService.registerNewUser(correctlyCapitalizedUsername, lowerCaseEmailAdress, password);
      playerService.enableUser(newPlayer);
      return new PlayerDetailedDto(newPlayer);

    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data!");

    }
  }

}
