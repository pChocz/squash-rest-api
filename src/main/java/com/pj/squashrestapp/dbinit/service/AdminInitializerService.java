package com.pj.squashrestapp.dbinit.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonAuthorities;
import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonHallOfFameSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueRoles;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayer;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayerCredentials;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.dbinit.jsondto.JsonXpPoints;
import com.pj.squashrestapp.dbinit.jsondto.JsonXpPointsForRound;
import com.pj.squashrestapp.dbinit.jsondto.util.JsonImportUtil;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.GsonUtil;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminInitializerService {

  private final XpPointsRepository xpPointsRepository;
  private final PlayerRepository playerRepository;
  private final LeagueRepository leagueRepository;
  private final AuthorityRepository authorityRepository;
  private final RoleForLeagueRepository roleForLeagueRepository;


  public boolean initialize(final String initXpPointsJsonContent,
                            final String initAllLeaguesJsonContent,
                            final String initCredentialsJsonContent) throws Exception {

    final Player adminPlayer = playerRepository.findByUsername("Admin");

    if (adminPlayer != null) {
      // means that Admin already exists so we cannot initialize it again
      return false;

    } else {
//      final long startTime = System.nanoTime();
      log.info("Initializing - BEGIN");

      persistStandardAuthorities();
      persistXpPointsFromJson(initXpPointsJsonContent);
      persistAllLeaguesFromJson(initAllLeaguesJsonContent);
      persistCredentials(initCredentialsJsonContent);

      log.info("Initializing - FINISHED");
//      TimeLogUtil.logFinish(startTime);

      return true;
    }
  }

  private void persistStandardAuthorities() {
    final Authority adminAuthority = new Authority(AuthorityType.ROLE_ADMIN);
    final Authority userAuthority = new Authority(AuthorityType.ROLE_USER);

    authorityRepository.save(adminAuthority);
    authorityRepository.save(userAuthority);
  }

  private void persistXpPointsFromJson(final String initXpPointsJsonContent) throws Exception {
    final JsonXpPoints xpPointsDto = new Gson().fromJson(initXpPointsJsonContent, JsonXpPoints.class);

    final List<XpPointsForRound> xpPoints = new ArrayList<>();
    for (final JsonXpPointsForRound jsonXpPointsForRound : xpPointsDto.getXpPointsForRound()) {
      final int[] players = jsonXpPointsForRound.buildPlayerSplitArray();
      final int[][] points = jsonXpPointsForRound.buildXpPointsArray();
      final XpPointsForRound xpPointsForRound = new XpPointsForRound(players, points);
      xpPoints.add(xpPointsForRound);
    }

    xpPointsRepository.saveAll(xpPoints);
  }

  private void persistAllLeaguesFromJson(final String initLeagueJsonContent) throws Exception {
    final Type listOfMyClassObject = new TypeToken<ArrayList<JsonLeague>>() {
    }.getType();
    final List<JsonLeague> jsonLeagues = GsonUtil.gsonWithDate().fromJson(initLeagueJsonContent, listOfMyClassObject);

    for (final JsonLeague jsonLeague : jsonLeagues) {
      final List<Player> allPlayers = buildPlayersList(jsonLeague);
      final List<String> allPlayersUsernames = allPlayers.stream().map(Player::getUsername).collect(Collectors.toList());
      final List<Player> alreadyExistingPlayers = playerRepository.findByUsernameIn(allPlayersUsernames);
      final List<String> alreadyExistingPlayersUsernames = alreadyExistingPlayers.stream().map(Player::getUsername).collect(Collectors.toList());
      final List<Player> newPlayers = allPlayers.stream().filter(player -> !alreadyExistingPlayersUsernames.contains(player.getUsername())).collect(Collectors.toList());

      // we need to persist newPlayers, but for alreadyExistingPlayers we only need to assign league role
      playerRepository.saveAll(newPlayers);

      final List<Player> properListOfPlayers = Stream
              .concat(
                      newPlayers.stream(),
                      alreadyExistingPlayers.stream())
              .collect(Collectors.toList());

      final League league = buildLeague(jsonLeague, properListOfPlayers);
      leagueRepository.save(league);

      createLeagueRoles(league);
    }
  }

  private void persistCredentials(final String initCredentialsJsonContent) throws Exception {
    final Type listOfMyClassObject = new TypeToken<ArrayList<JsonPlayerCredentials>>() {
    }.getType();
    final List<JsonPlayerCredentials> jsonSimpleCredentials = new Gson().fromJson(initCredentialsJsonContent, listOfMyClassObject);

    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);

    for (final JsonPlayerCredentials credentials : jsonSimpleCredentials) {
      final String username = credentials.getUsername();
      final String email = credentials.getEmail();

      final String passwordToPersist;
      if (credentials.getPasswordHashed() != null) {
        passwordToPersist = credentials.getPasswordHashed();
      } else {
        final String passwordPlain = credentials.getPassword();
        passwordToPersist = bCryptPasswordEncoder.encode(passwordPlain);
      }

      final Player player = playerRepository
              .fetchForAuthorizationByUsernameOrEmailUppercase(username.toUpperCase())
              .orElse(new Player(username));

      player.setEnabled(true);
      player.setEmail(email);
      player.setPassword(passwordToPersist);
      player.setUuid(credentials.getUuid());
      player.setPasswordSessionUuid(credentials.getPasswordSessionUuid());

      playerRepository.save(player);
      persistLeagueRoles(credentials, player);
      persistAuthorities(credentials, player);
    }
  }

  private void persistAuthorities(final JsonPlayerCredentials credentials, final Player player) {
    final List<JsonAuthorities> jsonAuthorities = credentials.getAuthorities();
    for (final JsonAuthorities jsonAuthority : jsonAuthorities) {
      final String authorityAsString = jsonAuthority.getAuthority();
      final AuthorityType authorityType = AuthorityType.valueOf(authorityAsString);
      final Authority authority = authorityRepository.findByType(authorityType);
      player.addAuthority(authority);
      authorityRepository.save(authority);
    }
  }

  private List<Player> buildPlayersList(final JsonLeague jsonLeague) {
    final Set<JsonPlayer> jsonPlayers = new LinkedHashSet<>();

    for (final JsonSeason season : jsonLeague.getSeasons()) {
      for (final JsonRound round : season.getRounds()) {
        for (final JsonRoundGroup group : round.getGroups()) {
          jsonPlayers.addAll(group.getPlayers());
        }
      }
    }

    final List<Player> players = new ArrayList<>();
    for (final JsonPlayer jsonPlayer : jsonPlayers) {
      final String username = jsonPlayer.getName();
      final String email = "__WILL_BE_OVERWRITTEN__" + UUID.randomUUID() + "__@xxx.xx";
      final Player player = new Player(username, email);
      player.setPassword("__WILL_BE_OVERWRITTEN__");
      player.setEnabled(true);

      players.add(player);
    }

    return players;
  }

  private League buildLeague(final JsonLeague jsonLeague, final List<Player> players) {
    final League league = JsonImportUtil.constructLeague(jsonLeague);

    for (final JsonHallOfFameSeason jsonHallOfFameSeason : jsonLeague.getHallOfFameSeasons()) {
      final HallOfFameSeason hallOfFameSeason = JsonImportUtil.constructHallOfFameSeason(jsonHallOfFameSeason);
      league.addHallOfFameSeason(hallOfFameSeason);
    }

    for (final JsonSeason jsonSeason : jsonLeague.getSeasons()) {
      final Season season = JsonImportUtil.constructSeason(jsonSeason);

      for (final JsonBonusPoint jsonBonusPoint : jsonSeason.getBonusPoints()) {
        final BonusPoint bonusPoint = JsonImportUtil.constructBonusPoints(jsonBonusPoint, players);
        season.addBonusPoint(bonusPoint);
      }

      for (final JsonRound jsonRound : jsonSeason.getRounds()) {
        final Round round = JsonImportUtil.constructRound(jsonRound);

        for (final JsonRoundGroup jsonRoundGroup : jsonRound.getGroups()) {
          final RoundGroup roundGroup = JsonImportUtil.constructRoundGroup(jsonRoundGroup);

          int matchNumber = 1;
          for (final JsonMatch jsonMatch : jsonRoundGroup.getMatches()) {
            final Match match = JsonImportUtil.constructMatch(jsonMatch, players);
            match.setNumber(matchNumber++);

            for (int i = 0; i < jsonMatch.getSets().size(); i++) {
              final int setNumber = i + 1;
              final JsonSetResult jsonSetResult = jsonMatch.getSets().get(i);
              final SetResult setResult = JsonImportUtil.constructSetResult(setNumber, jsonSetResult);
              match.addSetResult(setResult);
            }

            if (jsonMatch.getSets().size() == 2) {
              final SetResult setResult = JsonImportUtil.constructEmptySetResult(3);
              match.addSetResult(setResult);
            }

            roundGroup.addMatch(match);
          }
          round.addRoundGroup(roundGroup);
        }
        JsonImportUtil.setSplitForRound(round);
        season.addRound(round);
      }
      league.addSeason(season);
    }
    return league;
  }

  private void createLeagueRoles(final League league) {
    final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
    league.addRoleForLeague(playerRole);
    roleForLeagueRepository.save(playerRole);

    final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
    league.addRoleForLeague(moderatorRole);
    roleForLeagueRepository.save(moderatorRole);
  }

  private void persistLeagueRoles(final JsonPlayerCredentials simpleCredentials, final Player player) {
    final List<JsonLeagueRoles> jsonLeagueRoles = simpleCredentials.getLeagueRoles();
    for (final JsonLeagueRoles jsonLeagueRole : jsonLeagueRoles) {
      final String leagueName = jsonLeagueRole.getLeague();
      final String roleAsString = jsonLeagueRole.getRole();

      final League league = leagueRepository.findByName(leagueName);
      final LeagueRole role = LeagueRole.valueOf(roleAsString);

      final RoleForLeague leagueRole = roleForLeagueRepository.findByLeagueAndLeagueRole(league, role);
      player.addRole(leagueRole);
      roleForLeagueRepository.save(leagueRole);
    }
  }

}
