package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.config.security.playerpasswordreset.OnPasswordResetEvent;
import com.pj.squashrestapp.config.security.playerregistration.OnRegistrationCompleteEvent;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.service.PlayerService;
import com.pj.squashrestapp.service.TokenService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
public class UserAccessController {

  private final ApplicationEventPublisher eventPublisher;
  private final PlayerService playerService;
  private final TokenService tokenService;


  @GetMapping(value = "/reset-password-player/{passwordResetToken}")
  @ResponseBody
  PlayerDetailedDto getPlayerForPasswordReset(@PathVariable final UUID passwordResetToken) {
    final PlayerDetailedDto player = tokenService.extractPlayerByPasswordResetToken(passwordResetToken);
    return player;
  }


  @PostMapping(value = "/logout")
  @ResponseBody
  void logout(@RequestParam final String token) {
    log.info("User [{}] has logged out", GeneralUtil.extractSessionUsername());
  }


  @PostMapping(value = "/sign-up")
  @ResponseBody
  PlayerDetailedDto signUpPlayer(@RequestParam final String username,
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


  @PostMapping(value = "/request-password-reset")
  @ResponseBody
  void requestResetPassword(@RequestParam final String usernameOrEmail,
                            @RequestParam final String frontendUrl,
                            final HttpServletRequest request) {

    final Player player = playerService.getPlayer(usernameOrEmail);

    if (player != null) {
      eventPublisher.publishEvent(new OnPasswordResetEvent(player, request.getLocale(), frontendUrl));

    } else {

      // we are delaying execution to give indication
      // to the user that some process is running.
      try {
        TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(3 * 1000, 5 * 1000));
      } catch (final InterruptedException ie) {
        Thread.currentThread().interrupt();
      }

      // we are only logging it internally. Information
      // that the account does not exist does not need
      // to be passed to the frontend.
      log.error("Account does not exist. This information is not passed to the frontend");
    }
  }


  @PostMapping(value = "/confirm-password-reset")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void confirmResetPassword(@RequestParam final UUID token,
                            @RequestParam final String newPassword) {

    playerService.changePlayerPassword(token, newPassword);
  }


  @PostMapping("/confirm-registration")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void confirmRegistration(@RequestParam final String token) {
    playerService.activateUserWithToken(token);
  }

}