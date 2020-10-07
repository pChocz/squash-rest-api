package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.config.UserDetailsImpl;
import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.config.security.playerpasswordreset.OnPasswordResetEvent;
import com.pj.squashrestapp.config.security.playerregistration.OnRegistrationCompleteEvent;
import com.pj.squashrestapp.config.security.token.TokenConstants;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.service.PlayerService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

  private final ApplicationEventPublisher eventPublisher;
  private final PlayerService playerService;


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
  void logout(@RequestParam final String token) {
    playerService.blacklistToken(token);
    log.info("User [{}] has logged out. Token has been blacklisted", GeneralUtil.extractSessionUsername());
  }


  @PostMapping(value = "/signUp")
  @ResponseBody
  PlayerDetailedDto signUpPlayer(
          @RequestParam final String username,
          @RequestParam final String email,
          @RequestParam final String password,
          @RequestParam final String frontendUrl,
          final HttpServletRequest request) {

    final String correctlyCapitalizedUsername = GeneralUtil.buildProperUsername(username);
    final String lowerCaseEmailAdress = email.toLowerCase();

    final boolean isValid = playerService.isValidSignupData(correctlyCapitalizedUsername, lowerCaseEmailAdress, password);
    if (isValid) {
      final Player newPlayer = playerService.registerNewUser(correctlyCapitalizedUsername, lowerCaseEmailAdress, password);
      eventPublisher.publishEvent(new OnRegistrationCompleteEvent(newPlayer, request.getLocale(), frontendUrl));
      return new PlayerDetailedDto(newPlayer);
    }
    return null;
  }


  @PostMapping(value = "/requestPasswordReset")
  @ResponseBody
  void requestResetPassword(
          @RequestParam final String usernameOrEmail,
          @RequestParam final String frontendUrl,
          final HttpServletRequest request) {

    final Player player = playerService.getPlayer(usernameOrEmail);

    if (player != null) {
      eventPublisher.publishEvent(new OnPasswordResetEvent(player, request.getLocale(), frontendUrl));

    } else {

      // we are delaying execution to give indication
      // to the user that some process is running.
      try {
        TimeUnit.SECONDS.sleep(3);
      } catch (final InterruptedException ie) {
        Thread.currentThread().interrupt();
      }

      // we are only logging it internally. Information
      // that the account does not exist does not need
      // to be passed to the frontend.
      log.error("Account does not exist. This information is not passed to the frontend");
    }
  }


  @PostMapping(value = "/resetPassword")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void confirmResetPassword(@RequestParam final UUID token,
                            @RequestParam final String newPassword) {

    playerService.changePlayerPassword(token, newPassword);
  }


  @PostMapping("/confirmRegistration")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void confirmRegistration(@RequestParam final String token) {
    playerService.activateUserWithToken(token);
  }


//  @GetMapping(value = "/{playerId}")
//  @ResponseBody
//  @PreAuthorize("isAdmin()")
//  PlayerDetailedDto onePlayerInfoById(
//          @PathVariable final Long playerId) {
//
//    final PlayerDetailedDto usersBasicInfo = playerService.getPlayerInfo(playerId);
//    return usersBasicInfo;
//  }


//  @GetMapping
//  @ResponseBody
//  @PreAuthorize("isAdmin()")
//  List<PlayerDetailedDto> allPlayersInfo() {
//    final List<PlayerDetailedDto> usersBasicInfo = playerService.getAllPlayers();
//    return usersBasicInfo;
//  }


//  @GetMapping(value = "/league/{leagueId}")
//  @ResponseBody
//  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
//  List<PlayerDetailedDto> playersDetailedByLeagueId(
//          @PathVariable final Long leagueId) {
//
//    final List<PlayerDetailedDto> usersBasicInfo = playerService.getLeaguePlayers(leagueId);
//    return usersBasicInfo;
//  }


  // todo: Allign it to use UUIDs properly

  @PutMapping(value = "/{playerId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  PlayerDetailedDto assignLeagueRole(
          @PathVariable final Long playerId,
          @RequestParam final Long leagueId,
          @RequestParam final LeagueRole leagueRole) {

    final PlayerDetailedDto playerDetailedDto = playerService.assignLeagueRole(playerId, leagueId, leagueRole);
    return playerDetailedDto;
  }

}
