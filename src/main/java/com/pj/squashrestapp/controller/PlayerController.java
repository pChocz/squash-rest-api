package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.config.security.token.TokenConstants;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/players")
public class PlayerController {

  @Autowired
  private PlayerService playerService;

  @Autowired
  private EmailSendConfig emailSendConfig;

  @PostMapping(value = "/email")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  void sendEmail() {
    final String receiver = "p.j.choczynski@wp.pl";
    final String subject = "test subject";
    final String messageContent = "DUPA DUPA DUPA \nDUPA";
    emailSendConfig.sendEmail(receiver, subject, messageContent);
  }

  @GetMapping(value = "/me")
  @ResponseBody
  PlayerDetailedDto aboutMe() {
    final PlayerDetailedDto aboutMeInfo = playerService.getAboutMeInfo();
    return aboutMeInfo;
  }


  @DeleteMapping(value = "/cleanBlacklistedTokens")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  void cleanBlacklistedTokens() {
    final int numberOfRemovedTokens = playerService.removeExpiredBlacklistedTokensFromDb();
    log.info("Removed {} expired tokens.", numberOfRemovedTokens);
  }


  @PostMapping(value = "/logout")
  @ResponseBody
  void logout(@RequestHeader(name = TokenConstants.HEADER_STRING) final String bearerToken) {
    playerService.blacklistToken(bearerToken);
    log.info("Logged-out");
  }


  @PostMapping(value = "/sign-up")
  @ResponseBody
  PlayerDetailedDto signUpPlayer(
          @RequestParam("username") final String username,
          @RequestParam("email") final String email,
          @RequestParam("password") final String password) {

    final boolean isValid = playerService.isValidSignupData(username, email, password);
    if (isValid) {
      final Player newPlayer = playerService.registerNewUser(username, email, password);
      return new PlayerDetailedDto(newPlayer);
    }
    return null;
  }


  @GetMapping(value = "/{playerId}")
  @ResponseBody
  @PreAuthorize("isAdmin()")
  PlayerDetailedDto onePlayerInfoById(
          @PathVariable final Long playerId) {

    final PlayerDetailedDto usersBasicInfo = playerService.getPlayerInfo(playerId);
    return usersBasicInfo;
  }


  @GetMapping
  @ResponseBody
  @PreAuthorize("isAdmin()")
  List<PlayerDetailedDto> allPlayersInfo() {
    final List<PlayerDetailedDto> usersBasicInfo = playerService.getAllPlayers();
    return usersBasicInfo;
  }


  @GetMapping(value = "/league/{leagueId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  List<PlayerDetailedDto> byLeagueId(
          @PathVariable("leagueId") final Long leagueId) {

    final List<PlayerDetailedDto> usersBasicInfo = playerService.getLeaguePlayers(leagueId);
    return usersBasicInfo;
  }


  @PutMapping(value = "/{playerId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  PlayerDetailedDto assignLeagueRole(
          @PathVariable("playerId") final Long playerId,
          @RequestParam("leagueId") final Long leagueId,
          @RequestParam("leagueRole") final LeagueRole leagueRole) {

    final PlayerDetailedDto playerDetailedDto = playerService.assignLeagueRole(playerId, leagueId, leagueRole);
    return playerDetailedDto;
  }

}
