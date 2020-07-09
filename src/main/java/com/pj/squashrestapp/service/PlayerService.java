package com.pj.squashrestapp.service;

import com.google.gson.Gson;
import com.pj.squashrestapp.config.security.token.TokenConstants;
import com.pj.squashrestapp.controller.WrongSignupDataException;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.BlacklistedToken;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.dto.PlayerDetailedDto;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.BlacklistedTokensRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.util.PasswordStrengthValidator;
import com.pj.squashrestapp.util.UsernameValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

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
  private BlacklistedTokensRepository blacklistedTokensRepository;

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

  public Player registerNewUser(final String username, final String email, final String password) {
    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    final String hashedPassword = bCryptPasswordEncoder.encode(password);
    final Authority userAuthority = authorityRepository.findByType(AuthorityType.ROLE_USER);

    final Player player = new Player();
    player.setUsername(username);
    player.setEmail(email);
    player.setPassword(hashedPassword);
    player.addAuthority(userAuthority);
    playerRepository.save(player);

    return player;
  }

  public void blacklistToken(final String bearerToken) {
    final String token = bearerToken.replace(TokenConstants.TOKEN_PREFIX, "");
    final String tokenPayload = token.split("\\.")[1];
    final String tokenPayloadDecoded = new String(Base64.getDecoder().decode(tokenPayload));
    final Properties tokenProperties = new Gson().fromJson(tokenPayloadDecoded, Properties.class);
    final String expAsString = tokenProperties.getProperty(TokenConstants.EXPIRATION_PREFIX);
    final long expSeconds = Long.valueOf(expAsString);
    final LocalDateTime expirationDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(expSeconds),
            TimeZone.getTimeZone("UTC").toZoneId());

    final BlacklistedToken tokenToBlacklist = new BlacklistedToken();
    tokenToBlacklist.setToken(token);
    tokenToBlacklist.setExpirationDateTime(expirationDateTime);
    blacklistedTokensRepository.save(tokenToBlacklist);
  }

  public int removeExpiredBlacklistedTokensFromDb() {
    final LocalDateTime now = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId());

    final List<BlacklistedToken> expiredTokensToRemoveFromDb = blacklistedTokensRepository.findAllByExpirationDateTimeBefore(now);
    final int tokensCount = expiredTokensToRemoveFromDb.size();
    blacklistedTokensRepository.deleteAll(expiredTokensToRemoveFromDb);

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

}
