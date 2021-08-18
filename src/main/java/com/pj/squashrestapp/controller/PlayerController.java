package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.LeagueDtoSimple;
import com.pj.squashrestapp.dto.PlayerDetailedDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.PlayerService;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

/** */
@Slf4j
@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;

  @GetMapping(value = "/name-taken/{usernameOrEmail}")
  @ResponseBody
  boolean checkUsernameOrEmailTaken(@PathVariable final String usernameOrEmail) {
    final boolean playerExists = playerService.checkUsernameOrEmailTaken(usernameOrEmail);
    return playerExists;
  }

  @GetMapping(value = "/all")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  List<PlayerDetailedDto> extractAllPlayers() {
    final List<PlayerDetailedDto> allPlayers = playerService.getAllPlayers();
    return allPlayers;
  }

  @GetMapping(value = "/all-general")
  @ResponseBody
  List<PlayerDto> extractAllPlayersGeneralInfo() {
    final List<PlayerDto> allPlayers = playerService.getAllPlayersGeneral();
    return allPlayers;
  }

  @GetMapping(value = "/me")
  @ResponseBody
  PlayerDetailedDto aboutMe() {
    final PlayerDetailedDto aboutMeInfo = playerService.getAboutMeInfo();
    return aboutMeInfo;
  }

  @GetMapping(value = "/my-leagues")
  @ResponseBody
  Set<LeagueDtoSimple> myLeagues() {
    final Set<LeagueDtoSimple> myLeagues = playerService.getMyLeagues();
    return myLeagues;
  }

  @PostMapping(value = "/newEnabled")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  PlayerDetailedDto createNewPlayerEnabled(
      @RequestParam final String username,
      @RequestParam final String email,
      @RequestParam final String password) {

    final String correctlyCapitalizedUsername = GeneralUtil.buildProperUsername(username);
    final String lowerCaseEmailAdress = email.toLowerCase();

    final boolean isValid =
        playerService.isValidSignupData(
            correctlyCapitalizedUsername, lowerCaseEmailAdress, password);
    if (isValid) {
      final Player newPlayer =
          playerService.registerNewUser(
              correctlyCapitalizedUsername, lowerCaseEmailAdress, password);
      playerService.enableUser(newPlayer);
      return new PlayerDetailedDto(newPlayer);

    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data!");
    }
  }
}
