package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.SecretMethod;
import com.pj.squashrestapp.dto.PlayerDetailedDto;
import com.pj.squashrestapp.dto.TokenPair;
import com.pj.squashrestapp.hexagonal.email.SendEmailFacade;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.service.PlayerService;
import com.pj.squashrestapp.service.TokenCreateService;
import com.pj.squashrestapp.service.TokenRemovalService;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
public class UserAccessController {

  private final PlayerService playerService;
  private final TokenRemovalService tokenRemovalService;
  private final TokenCreateService tokenCreateService;
  private final SendEmailFacade sendEmailFacade;

  @GetMapping(value = "/reset-password-player/{passwordResetToken}")
  @ResponseBody
  PlayerDetailedDto getPlayerForPasswordReset(@PathVariable final UUID passwordResetToken) {
    final PlayerDetailedDto player =
        tokenRemovalService.extractPlayerByPasswordResetToken(passwordResetToken);
    return player;
  }

  @SecretMethod
  @PutMapping(value = "/change-my-password")
  TokenPair changeMyPassword(
      @RequestParam final String oldPassword, @RequestParam final String newPassword) {
    final TokenPair tokenPair =
        playerService.changeCurrentSessionPlayerPasswordAndGetNewTokens(oldPassword, newPassword);
    return tokenPair;
  }

  @PutMapping(value = "/change-my-email")
  void changeMyEmail(@RequestParam final String newEmail) {
    playerService.changeCurrentSessionPlayerEmail(newEmail);
  }

  @PutMapping(value = "/join-league")
  void joinNewLeague(@RequestParam final String leagueName) {
    playerService.joinNewLeague(leagueName);
  }

  @PutMapping(value = "/leave-league")
  void leaveLeague(@RequestParam final String leagueName) {
    playerService.leaveLeague(leagueName);
  }

  @PostMapping(value = "/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void logout() {
    log.info("User [{}] has logged out", GeneralUtil.extractSessionUsername());
  }

  @SecretMethod
  @PostMapping(value = "/sign-up")
  @ResponseBody
  PlayerDetailedDto signUpPlayer(
      @RequestParam final String username,
      @RequestParam final String email,
      @RequestParam final String password,
      @RequestParam final String frontendUrl,
      @RequestParam(defaultValue = "en") final String lang) {

    final String correctlyCapitalizedUsername = GeneralUtil.buildProperUsername(username);
    final String lowerCaseEmailAdress = email.toLowerCase();

    final boolean isValid =
        playerService.isValidSignupData(
            correctlyCapitalizedUsername, lowerCaseEmailAdress, password);
    if (isValid) {

      final Player newPlayer =
          playerService.registerNewUser(
              correctlyCapitalizedUsername, lowerCaseEmailAdress, password);

      final UUID token = UUID.randomUUID();
      playerService.createAndPersistVerificationToken(token, newPlayer);

      final String confirmationUrl = frontendUrl + "confirm-registration/" + token;

      sendEmailFacade.sendAccountActivationEmail(
          newPlayer.getEmail(), newPlayer.getUsername(), new Locale(lang), confirmationUrl);
      return new PlayerDetailedDto(newPlayer);
    }
    return null;
  }

  @PostMapping(value = "/request-password-reset")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void requestResetPassword(
      @RequestParam final String usernameOrEmail,
      @RequestParam final String frontendUrl,
      @RequestParam(defaultValue = "en") final String lang) {

    final Player player = playerService.getPlayer(usernameOrEmail);

    if (player != null) {
      final UUID token = UUID.randomUUID();
      playerService.createAndPersistPasswordResetToken(token, player);
      final String passwordResetUrl = frontendUrl + "reset-password/" + token;
      sendEmailFacade.sendPasswordResetEmail(
          player.getEmail(), player.getUsername(), new Locale(lang), passwordResetUrl);

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

  /** Invalidates all tokens (JWT and Refresh tokens) for all players without ADMIN authority. */
  @PostMapping(value = "/invalidate-all-tokens")
  @PreAuthorize("isAdmin()")
  void invalidateAllTokens() {
    playerService.invalidateAllTokens();
  }

  /**
   * Invalidates all tokens (JWT and Refresh tokens) for a single player
   *
   * @param playerUuid UUID of the player (can also be an ADMIN)
   */
  @PostMapping(value = "/invalidate-tokens-for-player/{playerUuid}")
  @PreAuthorize("isAdmin()")
  void invalidateTokensForPlayer(@PathVariable final UUID playerUuid) {
    playerService.invalidateTokensForPlayer(playerUuid);
  }

  @GetMapping(value = "/refresh-token/{oldRefreshTokenUuid}")
  @ResponseBody
  TokenPair refreshToken(@PathVariable final UUID oldRefreshTokenUuid) {
    final TokenPair tokenPair =
        tokenCreateService.attemptToCreateNewTokensPairUsingRefreshToken(oldRefreshTokenUuid);
    return tokenPair;
  }

  @SecretMethod
  @PostMapping(value = "/confirm-password-reset")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  TokenPair confirmResetPassword(
      @RequestParam final UUID passwordChangeToken, @RequestParam final String newPassword) {
    final TokenPair tokenPair =
        playerService.changeCurrentSessionPlayerPasswordAndGetNewTokens(
            passwordChangeToken, newPassword);
    return tokenPair;
  }

  @PostMapping("/confirm-registration")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void confirmRegistration(@RequestParam final UUID token) {
    playerService.activateUserWithToken(token);
  }
}
