package com.pj.squashrestapp.service;

import com.google.gson.Gson;
import com.pj.squashrestapp.config.security.token.TokenConstants;
import com.pj.squashrestapp.controller.WrongSignupDataException;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.BlacklistedToken;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.BlacklistedTokenRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.PasswordStrengthValidator;
import com.pj.squashrestapp.util.UsernameValidator;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/**
 *
 */
@Slf4j
@Service
public class PlayerService {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private AuthorityRepository authorityRepository;

  @Autowired
  private RoleForLeagueRepository roleForLeagueRepository;

  @Autowired
  private BlacklistedTokenRepository blacklistedTokenRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;


  @SuppressWarnings("OverlyComplexMethod")
  public boolean isValidSignupData(final String username, final String email, final String password) throws WrongSignupDataException {
    final List<Player> allPlayers = playerRepository.findAll();
    final Set<String> allUsernames = allPlayers.stream().map(Player::getUsername).collect(Collectors.toSet());
    final Set<String> allEmails = allPlayers.stream().map(Player::getEmail).collect(Collectors.toSet());

    final boolean usernameTaken = allUsernames.contains(username);
    final boolean emailTaken = allEmails.contains(email);

    final String message;

    if (usernameTaken && emailTaken) {
      message = "Both username and email are already taken, maybe you should log in instead?";

    } else if (emailTaken) {
      message = "Email is already taken!";

    } else if (usernameTaken) {
      message = "Username is already taken!";

    } else if (!UsernameValidator.isValid(username)) {
      message = "Username is not valid, it must contain 5-20 characters. Allowed characters are letters, numbers, dashes, underscores and spaces";

    } else if (!EmailValidator.getInstance().isValid(email)) {
      message = "Email is not valid!";

    } else if (!PasswordStrengthValidator.isValid(password)) {
      message = "Password is too weak. It must contain at least 5 characters, at least one upper case letter and at least one lower case letter. Whitespace characters are not allowed";

    } else {
      message = "";
    }

    if (message.isEmpty()) {
      return true;

    } else {
      throw new WrongSignupDataException(message);
    }
  }

  public Player getPlayer(final String usernameOrEmail) {
    return playerRepository.fetchForAuthorizationByUsernameOrEmail(usernameOrEmail).orElse(null);
  }

  public void createAndPersistVerificationToken(final String token, final Player user) {
    final VerificationToken verificationToken = new VerificationToken(token, user);
    verificationTokenRepository.save(verificationToken);
  }

  public void createAndPersistPasswordResetToken(final String token, final Player user) {
    final PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
    passwordResetTokenRepository.save(passwordResetToken);
  }

  public Player registerNewUser(final String username, final String email, final String password) {
    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);
    final String hashedPassword = bCryptPasswordEncoder.encode(password);
    final Authority userAuthority = authorityRepository.findByType(AuthorityType.ROLE_USER);

    final Player player = new Player(username, email);
    player.setPassword(hashedPassword);
    player.addAuthority(userAuthority);
    playerRepository.save(player);

    return player;
  }

  public void resendVerificationToken(final Player player) {
    // not implemented yet
  }


  public void blacklistToken(final String bearerToken) {
    final String token = bearerToken.replace(TokenConstants.TOKEN_PREFIX, "");
    final String tokenPayload = token.split("\\.")[1];
    final String tokenPayloadDecoded = new String(Base64.getDecoder().decode(tokenPayload));
    final Properties tokenProperties = new Gson().fromJson(tokenPayloadDecoded, Properties.class);
    final String expAsString = tokenProperties.getProperty(TokenConstants.EXPIRATION_PREFIX);
    final long expSeconds = Long.valueOf(expAsString);
    final LocalDateTime expirationDateTime = GeneralUtil.toLocalDateTimeUtc(expSeconds);

    final BlacklistedToken tokenToBlacklist = new BlacklistedToken();
    tokenToBlacklist.setToken(token);
    tokenToBlacklist.setExpirationDateTime(expirationDateTime);
    blacklistedTokenRepository.save(tokenToBlacklist);
  }

  public int removeExpiredBlacklistedTokensFromDb() {
    final LocalDateTime now = LocalDateTime.now(UTC_ZONE_ID);

    final List<BlacklistedToken> expiredTokensToRemoveFromDb = blacklistedTokenRepository.findAllByExpirationDateTimeBefore(now);
    final int tokensCount = expiredTokensToRemoveFromDb.size();
    blacklistedTokenRepository.deleteAll(expiredTokensToRemoveFromDb);

    return tokensCount;
  }

  public PlayerDetailedDto getAboutMeInfo() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.fetchForAuthorizationByUsernameOrEmail(auth.getName()).get();
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    return userBasicInfo;
  }

  public List<PlayerDetailedDto> getLeaguePlayers(final Long leagueId) {
    final List<Player> players = playerRepository.fetchForAuthorizationForLeague(leagueId);
    return players
            .stream()
            .map(PlayerDetailedDto::new)
            .collect(Collectors.toList());
  }

  public List<PlayerDetailedDto> getAllPlayers() {
    final List<Player> players = playerRepository.fetchForAuthorizationAll();
    return players
            .stream()
            .map(PlayerDetailedDto::new)
            .collect(Collectors.toList());
  }

  public PlayerDetailedDto getPlayerInfo(final Long playerId) {
    final Player player = playerRepository.fetchForAuthorizationById(playerId).get();
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    return userBasicInfo;
  }

  public PlayerDetailedDto assignLeagueRole(final Long playerId, final Long leagueId, final LeagueRole leagueRole) {
    final Player player = playerRepository.fetchForAuthorizationById(playerId).get();
    final League league = leagueRepository.findById(leagueId).get();
    final RoleForLeague roleForLeague = roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);
    player.addRole(roleForLeague);

    playerRepository.save(player);

    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  public void changePlayerPassword(final String token, final String newPassword) {
    final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

    if (passwordResetToken == null) {
      log.warn("It seems that we do not have matching token!");
      throw new RuntimeException("It seems that we do not have matching token!");

    } else if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationDateTime())) {
      log.warn("Password reset token has already expired. You must request new one!");

    } else if (!PasswordStrengthValidator.isValid(newPassword)) {
      throw new RuntimeException("Password is too weak. It must contain at least 5 characters, at least one upper case letter and at least one lower case letter. Whitespace characters are not allowed");

    } else {
      final Player player = passwordResetToken.getPlayer();
      final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);
      final String hashedPassword = bCryptPasswordEncoder.encode(newPassword);
      player.setPassword(hashedPassword);
      player.setPasswordSessionUuid(UUID.randomUUID());

      playerRepository.save(player);
      passwordResetTokenRepository.delete(passwordResetToken);

      log.info("Password for user {} has been succesfully changed.", player.getUsername());
    }
  }

  public void activateUserWithToken(final String token) {
    final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

    if (verificationToken == null) {
      log.warn("It seems that we do not have matching token!");

    } else if (LocalDateTime.now(UTC_ZONE_ID).isAfter(verificationToken.getExpirationDateTime())) {
      log.warn("Activation token has already expired. You must request sending new one!");

    } else {
      final Player player = verificationToken.getPlayer();
      player.setEnabled(true);
      playerRepository.save(player);
      verificationTokenRepository.delete(verificationToken);
      log.info("User {} has been succesfully activated.", player.getUsername());
    }
  }

}
