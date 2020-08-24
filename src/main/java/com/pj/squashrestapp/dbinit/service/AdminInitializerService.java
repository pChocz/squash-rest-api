package com.pj.squashrestapp.dbinit.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pj.squashrestapp.dbinit.jsondto.JsonAuthorities;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeagueRoles;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayerCredentials;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayerSimpleCredentials;
import com.pj.squashrestapp.dbinit.jsondto.JsonXpPoints;
import com.pj.squashrestapp.dbinit.jsondto.JsonXpPointsForRound;
import com.pj.squashrestapp.dbinit.jsondto.util.JsonImportUtil;
import com.pj.squashrestapp.dbinit.jsondto.JsonBonusPoint;
import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonHallOfFameSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonLeague;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayer;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
@Slf4j
@Service
public class AdminInitializerService {

  @Autowired
  private XpPointsRepository xpPointsRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private AuthorityRepository authorityRepository;

  @Autowired
  private RoleForLeagueRepository roleForLeagueRepository;


  public boolean initialize(final String initAdminJsonContent,
                            final String initXpPointsJsonContent,
                            final String initLeagueJsonContent,
                            final String initCredentialsJsonContent) throws Exception {

    final Player adminPlayer = playerRepository.findByUsername("Admin");

    if (adminPlayer != null) {
      // means that Admin already exists so we cannot initialize it again
      return false;

    } else {
      persistStandardAuthorities();
      persistAdminFromJson(initAdminJsonContent);
      persistXpPointsFromJson(initXpPointsJsonContent);
      persistEntireLeagueFromJson(initLeagueJsonContent);
      persistCredentials(initCredentialsJsonContent);
      return true;
    }
  }

  private void persistStandardAuthorities() {
    final Authority adminAuthority = new Authority(AuthorityType.ROLE_ADMIN);
    final Authority userAuthority = new Authority(AuthorityType.ROLE_USER);

    authorityRepository.save(adminAuthority);
    authorityRepository.save(userAuthority);
  }

  private void persistAdminFromJson(final String initAdminJsonContent) throws Exception {
    final JsonPlayerCredentials adminDto = new Gson().fromJson(initAdminJsonContent, JsonPlayerCredentials.class);

    final Player adminPlayer = new Player();
    adminPlayer.setEnabled(true);
    adminPlayer.setUsername(adminDto.getUsername());
    adminPlayer.setUuid(UUID.fromString(adminDto.getUuid()));
    adminPlayer.setPasswordSessionUuid(UUID.fromString(adminDto.getPasswordSessionUuid()));
    adminPlayer.setPassword(adminDto.getPasswordHashed());
    adminPlayer.setEmail(adminDto.getEmail());
    final Authority adminAuthority = authorityRepository.findByType(AuthorityType.ROLE_ADMIN);
    adminPlayer.addAuthority(adminAuthority);

    playerRepository.save(adminPlayer);
    authorityRepository.save(adminAuthority);
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

  private void persistEntireLeagueFromJson(final String initLeagueJsonContent) throws Exception {
    final JsonLeague jsonLeague = new Gson().fromJson(initLeagueJsonContent, JsonLeague.class);

    final List<Player> players = buildPlayersList(jsonLeague);
    playerRepository.saveAll(players);

    final League league = buildLeague(jsonLeague, players);
    leagueRepository.save(league);

    createLeagueRoles(league);
  }

  private void persistCredentials(final String initCredentialsJsonContent) throws Exception {
    final Type listOfMyClassObject = new TypeToken<ArrayList<JsonPlayerSimpleCredentials>>() {
    }.getType();
    final List<JsonPlayerSimpleCredentials> jsonSimpleCredentials = new Gson().fromJson(initCredentialsJsonContent, listOfMyClassObject);

    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);

    for (final JsonPlayerSimpleCredentials simpleCredentials : jsonSimpleCredentials) {
      final String username = simpleCredentials.getUsername();
      final String email = simpleCredentials.getEmail();
      final String passwordPlain = simpleCredentials.getPassword();
      final String passwordHashed = bCryptPasswordEncoder.encode(passwordPlain);

      final Player player = playerRepository.fetchForAuthorizationByUsernameOrEmail(username).orElse(null);
      player.setEmail(email);
      player.setPassword(passwordHashed);

      persistLeagueRoles(simpleCredentials, player);
      persistAuthorities(simpleCredentials, player);

      playerRepository.save(player);
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

    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);

    final List<Player> players = new ArrayList<>();
    for (final JsonPlayer jsonPlayer : jsonPlayers) {
      final String username = jsonPlayer.getName();
      final String email = jsonPlayer.getName().replace(" ", "_").toLowerCase() + "@gmail.com";
      final Player player = new Player(username, email);

      final String firstNameLowercase = username.contains(" ")
              ? username.substring(0, username.indexOf(" ")).toLowerCase()
              : username.toLowerCase();
      player.setPassword(bCryptPasswordEncoder.encode(firstNameLowercase));
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

  private void persistLeagueRoles(final JsonPlayerSimpleCredentials simpleCredentials, final Player player) {
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

  private void persistAuthorities(final JsonPlayerSimpleCredentials simpleCredentials, final Player player) {
    final List<JsonAuthorities> jsonAuthorities = simpleCredentials.getAuthorities();
    for (final JsonAuthorities jsonAuthority : jsonAuthorities) {
      final String authorityAsString = jsonAuthority.getAuthority();
      final AuthorityType authorityType = AuthorityType.valueOf(authorityAsString);
      final Authority authority = authorityRepository.findByType(authorityType);
      player.addAuthority(authority);
      authorityRepository.save(authority);
    }
  }

}
