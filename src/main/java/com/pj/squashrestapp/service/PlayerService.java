package com.pj.squashrestapp.service;

import static com.pj.squashrestapp.config.security.token.TokenConstants.VERIFICATION_TOKEN_EXPIRATION_TIME_DAYS;
import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

import com.pj.squashrestapp.config.exceptions.EmailAlreadyTakenException;
import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.config.exceptions.PasswordDoesNotMatchException;
import com.pj.squashrestapp.config.exceptions.WrongSignupDataException;
import com.pj.squashrestapp.dto.PlayerDetailedDto;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RefreshToken;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RefreshTokenRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import com.pj.squashrestapp.util.PasswordStrengthValidator;
import com.pj.squashrestapp.util.UsernameValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final LeagueRepository leagueRepository;
  private final AuthorityRepository authorityRepository;
  private final RoleForLeagueRepository roleForLeagueRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;

  @SuppressWarnings("OverlyComplexMethod")
  public boolean isValidSignupData(final String username, final String email, final String password)
      throws WrongSignupDataException {
    final List<Player> allPlayers = playerRepository.findAll();
    final Set<String> allUsernames =
        allPlayers.stream().map(Player::getUsername).collect(Collectors.toSet());
    final Set<String> allEmails =
        allPlayers.stream().map(Player::getEmail).collect(Collectors.toSet());

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
      message =
          "Username is not valid, it must contain 5-20 characters. Allowed characters are letters, numbers, dashes, underscores and spaces";

    } else if (!EmailValidator.getInstance().isValid(email)) {
      message = "Email is not valid!";

    } else if (!PasswordStrengthValidator.isValid(password)) {
      message = "Password is too weak or too long. It must contain 5-100 characters.";

    } else {
      message = "";
    }

    if (message.isEmpty()) {
      return true;

    } else {
      throw new WrongSignupDataException(message);
    }
  }

  public void createAndPersistVerificationToken(final UUID token, final Player user) {
    final LocalDateTime expirationDateTime =
        LocalDateTime.now(UTC_ZONE_ID).plusDays(VERIFICATION_TOKEN_EXPIRATION_TIME_DAYS);
    final VerificationToken verificationToken =
        new VerificationToken(token, user, expirationDateTime);
    verificationTokenRepository.save(verificationToken);
  }

  public void createAndPersistPasswordResetToken(final UUID token, final Player user) {
    final PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
    passwordResetTokenRepository.save(passwordResetToken);
  }

  public Player registerNewUser(final String username, final String email, final String password) {
    final String hashedPassword = passwordEncoder.encode(password);
    final Authority userAuthority = authorityRepository.findByType(AuthorityType.ROLE_USER);

    final Player player = new Player(username, email);
    player.setPassword(hashedPassword);
    player.addAuthority(userAuthority);
    playerRepository.save(player);
    authorityRepository.save(userAuthority);

    return player;
  }

  public void enableUser(final Player player) {
    player.setEnabled(true);
    playerRepository.save(player);
  }

  public void resendVerificationToken(final Player player) {
    // todo: implement!
  }

  public List<PlayerDetailedDto> getAllPlayers() {
    final List<Player> allPlayers = playerRepository.findAll();
    final List<PlayerDetailedDto> allPlayersDetailedInfo =
        allPlayers.stream().map(PlayerDetailedDto::new).collect(Collectors.toList());
    return allPlayersDetailedInfo;
  }

  public PlayerDetailedDto getAboutMeInfo() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player =
        playerRepository
            .fetchForAuthorizationByUsernameOrEmailUppercase(auth.getName().toUpperCase())
            .orElseThrow();
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    return userBasicInfo;
  }

  public void changeCurrentSessionPlayerEmail(final String newEmail) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.findByUsername(auth.getName());

    final List<Player> allPlayers = playerRepository.findAll();
    final Set<String> allEmails =
        allPlayers.stream().map(Player::getEmail).collect(Collectors.toSet());
    final boolean emailValid = !allEmails.contains(newEmail);

    if (emailValid) {
      player.setEmail(newEmail);
      playerRepository.save(player);
      log.info(
          "Email for user {} has been succesfully changed to {}.",
          player.getUsername(),
          player.getEmail());

    } else {
      log.warn("Attempt to change email but it's already taken");
      throw new EmailAlreadyTakenException("Email already taken!");
    }
  }

  @Transactional
  public void joinNewLeague(final String leagueName) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = getPlayer(auth.getName());
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    final boolean isPlayerForLeagueAlready = userBasicInfo.isPlayerForLeague(leagueName);

    if (isPlayerForLeagueAlready) {
      throw new GeneralBadRequestException("Already player of this league");
    }

    final League league = leagueRepository.findByName(leagueName);
    final boolean leagueExists = league != null;

    if (!leagueExists) {
      throw new GeneralBadRequestException("Such league does not exist");
    }

    assignLeagueRole(player.getUuid(), league.getUuid(), LeagueRole.PLAYER);
  }

  public Player getPlayer(final String usernameOrEmail) {
    return playerRepository
        .fetchForAuthorizationByUsernameOrEmailUppercase(usernameOrEmail.toUpperCase())
        .orElse(null);
  }

  @Transactional
  public PlayerDetailedDto assignLeagueRole(
      final UUID playerUuid, final UUID leagueUuid, final LeagueRole leagueRole) {
    final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).orElseThrow();
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final RoleForLeague roleForLeague =
        roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);
    player.addRole(roleForLeague);

    playerRepository.save(player);

    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  @Transactional
  public void leaveLeague(final String leagueName) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = getPlayer(auth.getName());
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    final boolean isPlayerForLeague = userBasicInfo.isPlayerForLeague(leagueName);

    if (!isPlayerForLeague) {
      throw new GeneralBadRequestException("Not a player of this league");
    }

    final League league = leagueRepository.findByName(leagueName);
    final boolean leagueExists = league != null;

    if (!leagueExists) {
      throw new GeneralBadRequestException("Such league does not exist");
    }

    unassignLeagueRole(player.getUuid(), league.getUuid(), LeagueRole.PLAYER);
  }

  @Transactional
  public PlayerDetailedDto unassignLeagueRole(
      final UUID playerUuid, final UUID leagueUuid, final LeagueRole leagueRole) {
    final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).orElseThrow();
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final RoleForLeague roleForLeague =
        roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);
    player.removeRole(roleForLeague);

    playerRepository.save(player);

    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  public void changeCurrentSessionPlayerPassword(
      final String oldPassword, final String newPassword) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.findByUsername(auth.getName());
    final String oldPasswordHashed = player.getPassword();
    final boolean oldPasswordMatches = passwordEncoder.matches(oldPassword, oldPasswordHashed);

    if (oldPasswordMatches) {
      final String hashedPassword = passwordEncoder.encode(newPassword);
      player.setPassword(hashedPassword);
      player.setPasswordSessionUuid(UUID.randomUUID());
      playerRepository.save(player);
      log.info("Password for user {} has been succesfully changed.", player.getUsername());

    } else {
      log.warn("Attempt to change password but old password does not match");
      throw new PasswordDoesNotMatchException("Old password does not match!");
    }
  }

  public void changeCurrentSessionPlayerPassword(final UUID token, final String newPassword) {
    final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

    if (passwordResetToken == null) {
      log.warn("It seems that we do not have matching token!");
      throw new GeneralBadRequestException("It seems that we do not have matching token!");

    } else if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationDateTime())) {
      throw new GeneralBadRequestException(
          "Password reset token has already expired. You must request new one!");

    } else if (!PasswordStrengthValidator.isValid(newPassword)) {
      throw new GeneralBadRequestException(
          "Password is too weak (or too long). It must contain at least 5 characters (and not more than 100).");

    } else {
      final Player player = passwordResetToken.getPlayer();
      final String hashedPassword = passwordEncoder.encode(newPassword);
      player.setPassword(hashedPassword);
      player.setPasswordSessionUuid(UUID.randomUUID());

      playerRepository.save(player);
      passwordResetTokenRepository.delete(passwordResetToken);

      log.info("Password for user {} has been succesfully changed.", player.getUsername());
    }
  }

  public void activateUserWithToken(final UUID token) {
    final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

    if (verificationToken == null) {
      log.warn("It seems that we do not have matching token!");
      throw new RuntimeException("No matching token!");

    } else if (LocalDateTime.now(UTC_ZONE_ID).isAfter(verificationToken.getExpirationDateTime())) {
      log.warn("Activation token has already expired. You must request sending new one!");
      throw new RuntimeException("Token has expired!");

    } else {
      final Player player = verificationToken.getPlayer();
      player.setEnabled(true);
      playerRepository.save(player);
      verificationTokenRepository.delete(verificationToken);
      log.info("User {} has been succesfully activated.", player.getUsername());
    }
  }

  public void invalidateAllTokens() {
    final List<Player> nonAdminPlayers =
        playerRepository.findAll().stream()
            .filter(this::isNonAdminPlayer)
            .collect(Collectors.toList());

    for (final Player player : nonAdminPlayers) {
      player.setPasswordSessionUuid(UUID.randomUUID());
    }

    playerRepository.saveAll(nonAdminPlayers);

    final List<RefreshToken> playerRefreshTokens =
        refreshTokenRepository.findAllByPlayerIn(nonAdminPlayers);
    refreshTokenRepository.deleteAll(playerRefreshTokens);
  }

  private boolean isNonAdminPlayer(final Player player) {
    return player.getAuthorities().stream()
        .noneMatch(authority -> authority.getType() == AuthorityType.ROLE_ADMIN);
  }

  public void invalidateTokensForPlayer(final UUID playerUuid) {
    final Player player = playerRepository.findByUuid(playerUuid);
    player.setPasswordSessionUuid(UUID.randomUUID());
    playerRepository.save(player);

    final List<RefreshToken> playerRefreshTokens = refreshTokenRepository.findAllByPlayer(player);
    refreshTokenRepository.deleteAll(playerRefreshTokens);
  }
}
