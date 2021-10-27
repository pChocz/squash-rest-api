package com.pj.squashrestapp.service;

import static com.pj.squashrestapp.config.security.token.TokenConstants.VERIFICATION_TOKEN_EXPIRATION_TIME_DAYS;
import static com.pj.squashrestapp.util.GeneralUtil.UTC_ZONE_ID;

import com.pj.squashrestapp.config.exceptions.EmailAlreadyTakenException;
import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.config.exceptions.PasswordDoesNotMatchException;
import com.pj.squashrestapp.config.exceptions.WrongSignupDataException;
import com.pj.squashrestapp.dto.LeagueDtoSimple;
import com.pj.squashrestapp.dto.PlayerDetailedDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.TokenPair;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
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
import com.pj.squashrestapp.repository.VerificationTokenRepository;
import com.pj.squashrestapp.util.EmojiUtil;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.PasswordStrengthValidator;
import com.pj.squashrestapp.util.UsernameValidator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {

  private final LeagueRolesService leagueRolesService;

  private final PlayerRepository playerRepository;
  private final LeagueRepository leagueRepository;
  private final AuthorityRepository authorityRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenCreateService tokenCreateService;
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
    player.setEmoji(EmojiUtil.getRandom());
    player.setLocale("en");
    player.setRegistrationDateTime(LocalDateTime.now());
    player.setLastLoggedInDateTime(LocalDateTime.now());
    player.setNonLocked(true);
    player.setSuccessfulLoginAttempts(0L);
    player.setWantsEmails(false);
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

  public List<PlayerDto> getAllPlayersGeneral() {
    final List<Player> allPlayers = playerRepository.findAllRaw();
    final List<PlayerDto> allPlayersDetailedInfo =
        allPlayers.stream().map(PlayerDto::new).collect(Collectors.toList());
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

  public Player getPlayer(final String usernameOrEmail) {
    return playerRepository
        .fetchForAuthorizationByUsernameOrEmailUppercase(usernameOrEmail.toUpperCase())
        .orElse(null);
  }

  public TokenPair changeCurrentSessionPlayerPasswordAndGetNewTokens(
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

      final List<RefreshToken> playerRefreshTokens = refreshTokenRepository.findAllByPlayer(player);
      refreshTokenRepository.deleteAll(playerRefreshTokens);

      final TokenPair tokenPair = tokenCreateService.createTokensPairForPlayer(player);
      return tokenPair;

    } else {
      log.warn("Attempt to change password but old password does not match");
      throw new PasswordDoesNotMatchException("Old password does not match!");
    }
  }

  public TokenPair changeCurrentSessionPlayerPasswordAndGetNewTokens(
      final UUID token, final String newPassword) {
    final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

    if (passwordResetToken == null) {
      log.warn("It seems that we do not have matching token!");
      throw new GeneralBadRequestException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN);

    } else if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationDateTime())) {
      throw new GeneralBadRequestException(ErrorCode.EXPIRED_PASSWORD_RESET_TOKEN);

    } else if (!PasswordStrengthValidator.isValid(newPassword)) {
      throw new GeneralBadRequestException(
          "Password is too weak (or too long). It must contain at least 5 characters (and not more than 100).");
    }

    final Player player = passwordResetToken.getPlayer();
    final String hashedPassword = passwordEncoder.encode(newPassword);
    player.setPassword(hashedPassword);
    player.setPasswordSessionUuid(UUID.randomUUID());

    playerRepository.save(player);
    passwordResetTokenRepository.delete(passwordResetToken);

    log.info("Password for user {} has been succesfully changed.", player.getUsername());
    final TokenPair tokenPair = tokenCreateService.createTokensPairForPlayer(player);
    return tokenPair;
  }

  public void activateUserWithToken(final UUID token) {
    final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

    if (verificationToken == null) {
      throw new GeneralBadRequestException(ErrorCode.INVALID_ACCOUNT_ACTIVATION_TOKEN);

    } else if (LocalDateTime.now(UTC_ZONE_ID).isAfter(verificationToken.getExpirationDateTime())) {
      throw new GeneralBadRequestException(ErrorCode.EXPIRED_ACCOUNT_ACTIVATION_TOKEN);
    }

    final Player player = verificationToken.getPlayer();
    player.setEnabled(true);
    playerRepository.save(player);
    verificationTokenRepository.delete(verificationToken);
    log.info("User {} has been succesfully activated.", player.getUsername());
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

  public boolean checkUsernameOrEmailTaken(final String usernameOrEmail) {
    final List<Player> players = playerRepository.findAllRaw();
    final boolean exists = players.stream().anyMatch(playerNameOrEmailPredicate(usernameOrEmail));
    return exists;
  }

  private Predicate<Player> playerNameOrEmailPredicate(final String usernameOrEmail) {
    return player ->
        player.getUsername().equalsIgnoreCase(usernameOrEmail.trim())
            || player.getEmail().equalsIgnoreCase(usernameOrEmail.trim());
  }

  public Set<LeagueDtoSimple> getMyLeagues() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player =
        playerRepository
            .fetchForAuthorizationByUsernameOrEmailUppercase(auth.getName().toUpperCase())
            .orElseThrow();

    final Set<LeagueDtoSimple> myLeagues =
        new TreeSet<>(Comparator.comparing(LeagueDtoSimple::getDateOfCreation));
    for (final RoleForLeague role : player.getRoles()) {
      if (role.getLeagueRole() == LeagueRole.PLAYER) {
        myLeagues.add(new LeagueDtoSimple(role.getLeague()));
      }
    }

    return myLeagues;
  }

  public PlayerDetailedDto extractPlayerByPasswordResetToken(final UUID token) {
    final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
    if (passwordResetToken == null) {
      throw new GeneralBadRequestException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN);

    } else if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationDateTime())) {
      throw new GeneralBadRequestException(ErrorCode.EXPIRED_PASSWORD_RESET_TOKEN);
    }

    final UUID playerUuid = passwordResetToken.getPlayer().getUuid();
    final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).get();
    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  public List<String> getAllEmojis() {
    final List<String> allEmojis = EmojiUtil.EMOJIS;
    return allEmojis;
  }

  public void changeEmojiForCurrentPlayer(final String newEmoji) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.findByUsername(auth.getName());
    PlayerService.this.changeEmojiForPlayer(newEmoji, player);
  }

  public void changeEmojiForPlayer(final UUID playerUuid, final String newEmoji) {
    final Player player = playerRepository.findByUuid(playerUuid);
    changeEmojiForPlayer(newEmoji, player);
  }

  private void changeEmojiForPlayer(final String newEmoji, final Player player) {
    if (EmojiUtil.EMOJIS.contains(newEmoji)) {
      player.setEmoji(newEmoji);
      playerRepository.save(player);
      log.info("Emoji {} changed for player {}", newEmoji, player.getUsername());
    }
  }

  public PlayerDetailedDto getPlayerDetailedInfo(final UUID playerUuid) {
    final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).get();
    final PlayerDetailedDto playerDetailedDto = new PlayerDetailedDto(player);
    return playerDetailedDto;
  }

  public void changeEachIfPresent(
      final UUID playerUuid,
      final Optional<Boolean> nonLockedOptional,
      final Optional<Boolean> wantsEmailsOptional,
      final Optional<Boolean> enabledOptional,
      final Optional<String> usernameOptional,
      final Optional<String> emailOptional) {
    final Player player = playerRepository.findByUuid(playerUuid);

    nonLockedOptional.ifPresent(player::setNonLocked);
    wantsEmailsOptional.ifPresent(player::setWantsEmails);
    enabledOptional.ifPresent(player::setEnabled);
    usernameOptional.ifPresent(player::setUsername);
    emailOptional.ifPresent(player::setEmail);

    playerRepository.save(player);
  }
}
