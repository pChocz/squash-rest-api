package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.WrongSignupDataException;
import com.pj.squashrestapp.config.security.token.TokenConstants;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.PasswordResetToken;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.VerificationToken;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PasswordResetTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import com.pj.squashrestapp.util.PasswordStrengthValidator;
import com.pj.squashrestapp.util.UsernameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pj.squashrestapp.config.security.token.TokenConstants.VERIFICATION_TOKEN_EXPIRATION_TIME_DAYS;
import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

/**
 *
 */
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

  public Player getPlayer(final String usernameOrEmail) {
    return playerRepository.fetchForAuthorizationByUsernameOrEmailUppercase(usernameOrEmail.toUpperCase()).orElse(null);
  }

  public void createAndPersistVerificationToken(final UUID token, final Player user) {
    final LocalDateTime expirationDateTime = LocalDateTime.now(UTC_ZONE_ID).plusDays(VERIFICATION_TOKEN_EXPIRATION_TIME_DAYS);
    final VerificationToken verificationToken = new VerificationToken(token, user, expirationDateTime);
    verificationTokenRepository.save(verificationToken);
  }

  public void createAndPersistPasswordResetToken(final UUID token, final Player user) {
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
    final List<PlayerDetailedDto> allPlayersDetailedInfo = allPlayers
            .stream()
            .map(PlayerDetailedDto::new)
            .collect(Collectors.toList());
    return allPlayersDetailedInfo;
  }

  public PlayerDetailedDto getAboutMeInfo() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.fetchForAuthorizationByUsernameOrEmailUppercase(auth.getName().toUpperCase()).orElseThrow();
    final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
    return userBasicInfo;
  }

  @Transactional
  public PlayerDetailedDto unassignLeagueRole(final UUID playerUuid, final UUID leagueUuid, final LeagueRole leagueRole) {
    final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).orElseThrow();
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final RoleForLeague roleForLeague = roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);
    player.removeRole(roleForLeague);

    playerRepository.save(player);

    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  @Transactional
  public PlayerDetailedDto assignLeagueRole(final UUID playerUuid, final UUID leagueUuid, final LeagueRole leagueRole) {
    final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).orElseThrow();
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final RoleForLeague roleForLeague = roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);
    player.addRole(roleForLeague);

    playerRepository.save(player);

    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  public void changeCurrentSessionPlayerPassword(final String oldPassword, final String newPassword) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.findByUsername(auth.getName());
    final String oldPasswordHashed = player.getPassword();
    final boolean oldPasswordMatches = BCrypt.checkpw(oldPassword, oldPasswordHashed);

    if (oldPasswordMatches) {
      final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);
      final String hashedPassword = bCryptPasswordEncoder.encode(newPassword);
      player.setPassword(hashedPassword);
      playerRepository.save(player);
      log.info("Password for user {} has been succesfully changed.", player.getUsername());

    } else {
      log.warn("Attempt to change password but old password does not match");
      throw new RuntimeException("Sorry, your old password does not match!");
    }
  }

  public void changeCurrentSessionPlayerPassword(final UUID token, final String newPassword) {
    final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

    if (passwordResetToken == null) {
      log.warn("It seems that we do not have matching token!");
      throw new RuntimeException("It seems that we do not have matching token!");

    } else if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationDateTime())) {
      throw new RuntimeException("Password reset token has already expired. You must request new one!");

    } else if (!PasswordStrengthValidator.isValid(newPassword)) {
      throw new RuntimeException("Password is too weak. It must contain at least 5 characters.");

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
    playerRepository
            .findAll()
            .stream()
            .filter(this::isNonAdminPlayer)
            .forEach(player -> {
              player.setPasswordSessionUuid(UUID.randomUUID());
              playerRepository.save(player);
            });
  }

  private boolean isNonAdminPlayer(final Player player) {
    return player
            .getAuthorities()
            .stream()
            .noneMatch(authority -> authority.getType() == AuthorityType.ROLE_ADMIN);
  }

}
