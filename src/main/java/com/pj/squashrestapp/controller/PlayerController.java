package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.LeagueDtoSimple;
import com.pj.squashrestapp.dto.PlayerDetailedDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.PlayerService;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.ResponseStatus;
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
  boolean checkUsernameOrEmailTaken(@PathVariable final String usernameOrEmail) {
    final boolean usernameOrEmailTaken = playerService.checkUsernameOrEmailTaken(usernameOrEmail);
    return usernameOrEmailTaken;
  }

  @GetMapping(value = "/emoji")
  List<String> getAllEmojis() {
    final List<String> allEmojis = playerService.getAllEmojis();
    return allEmojis;
  }

  @PostMapping(value = "/emoji")
  @ResponseStatus(HttpStatus.OK)
  void updateMyEmoji(@RequestParam final String newEmoji) {
    playerService.changeEmojiForCurrentPlayer(newEmoji);
  }

  @PostMapping(value = "/emoji/{playerUuid}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isAdmin()")
  void updateEmojiForPlayer(@PathVariable final UUID playerUuid, @RequestParam final String newEmoji) {
    playerService.changeEmojiForPlayer(playerUuid, newEmoji);
  }

  @GetMapping(value = "/all")
  @PreAuthorize("isAdmin()")
  List<PlayerDetailedDto> getAllPlayers() {
    final List<PlayerDetailedDto> allPlayers = playerService.getAllPlayers();
    return allPlayers;
  }

  @GetMapping(value = "/all-general")
  List<PlayerDto> getAllPlayersGeneralInfo() {
    final List<PlayerDto> allPlayers = playerService.getAllPlayersGeneral();
    return allPlayers;
  }

  @GetMapping(value = "/me")
  PlayerDetailedDto getAboutMe() {
    final PlayerDetailedDto aboutMeInfo = playerService.getAboutMeInfo();
    return aboutMeInfo;
  }

  @GetMapping(value = "/{playerUuid}")
  @PreAuthorize("isAdmin()")
  PlayerDetailedDto getPlayerDetailedInfo(@PathVariable final UUID playerUuid) {
    final PlayerDetailedDto playerDetailedDto = playerService.getPlayerDetailedInfo(playerUuid);
    return playerDetailedDto;
  }

  @GetMapping(value = "/my-leagues")
  Set<LeagueDtoSimple> getMyLeagues() {
    final Set<LeagueDtoSimple> myLeagues = playerService.getMyLeagues();
    return myLeagues;
  }

  @PutMapping(value = "/{playerUuid}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("isAdmin()")
  void updatePlayerParameters(
      @PathVariable final UUID playerUuid,
      @RequestParam final Optional<Boolean> nonLocked,
      @RequestParam final Optional<Boolean> wantsEmails,
      @RequestParam final Optional<Boolean> enabled,
      @RequestParam final Optional<String> username,
      @RequestParam final Optional<String> email) {
    playerService.changeEachIfPresent(
        playerUuid,
        nonLocked,
        wantsEmails,
        enabled,
        username,
        email);
  }

  @PostMapping(value = "/newEnabled")
  @PreAuthorize("isAdmin()")
  PlayerDetailedDto createPlayerEnabled(
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
