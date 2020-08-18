package com.pj.squashrestapp.dbinit.service;

import com.pj.squashrestapp.dbinit.dto.InitPlayer;
import com.pj.squashrestapp.dbinit.dto.InitXpPoints;
import com.pj.squashrestapp.dbinit.dto.XpPointsForRoundDto;
import com.pj.squashrestapp.dbinit.xml.FromXmlConstructUtil;
import com.pj.squashrestapp.dbinit.xml.entities.XmlBonus;
import com.pj.squashrestapp.dbinit.xml.entities.XmlGroup;
import com.pj.squashrestapp.dbinit.xml.entities.XmlHallOfFameSeason;
import com.pj.squashrestapp.dbinit.xml.entities.XmlLeague;
import com.pj.squashrestapp.dbinit.xml.entities.XmlMatch;
import com.pj.squashrestapp.dbinit.xml.entities.XmlPlayer;
import com.pj.squashrestapp.dbinit.xml.entities.XmlRound;
import com.pj.squashrestapp.dbinit.xml.entities.XmlSeason;
import com.pj.squashrestapp.dbinit.xml.entities.XmlSet;
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
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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


  public boolean initialize(final String initAdminXmlContent,
                         final String initXpPointsXmlContent,
                         final String initLeagueXmlContent) throws Exception {

    final Player adminPlayer = playerRepository.findByUsername("Admin");
    if (adminPlayer != null) {
      // means that Admin already exists so we cannot initialize it again
      return false;

    } else {
      persistStandardAuthorities();
      persistAdminFromXml(initAdminXmlContent);
      persistXpPointsFromXml(initXpPointsXmlContent);
      persistEntireLeague(initLeagueXmlContent);
      return true;
    }
  }

  private void persistStandardAuthorities() {
    final Authority adminAuthority = new Authority(AuthorityType.ROLE_ADMIN);
    final Authority userAuthority = new Authority(AuthorityType.ROLE_USER);

    authorityRepository.save(adminAuthority);
    authorityRepository.save(userAuthority);
  }

  private void persistAdminFromXml(final String initAdminXmlContent) throws Exception {
    final InitPlayer adminDto = new InitPlayer();
    new Persister().read(adminDto, initAdminXmlContent);

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

  private void persistXpPointsFromXml(final String initXpPointsXmlContent) throws Exception {
    final InitXpPoints xpPointsDto = new InitXpPoints();
    new Persister().read(xpPointsDto, initXpPointsXmlContent);

    final List<XpPointsForRound> xpPoints = new ArrayList<>();
    for (final XpPointsForRoundDto xpPointsForRoundDto : xpPointsDto.getXpPointsForRound()) {
      final int[] players = xpPointsForRoundDto.buildPlayerSplitArray();
      final int[][] points = xpPointsForRoundDto.buildXpPointsArray();
      final XpPointsForRound xpPointsForRound = new XpPointsForRound(players, points);
      xpPoints.add(xpPointsForRound);
    }

    xpPointsRepository.saveAll(xpPoints);
  }

  private void persistEntireLeague(final String initLeagueXmlContent) throws Exception {
    final XmlLeague xmlLeague = new XmlLeague();
    new Persister().read(xmlLeague, initLeagueXmlContent);

    final List<Player> players = buildPlayersList(xmlLeague);
    playerRepository.saveAll(players);

    final League league = buildLeague(xmlLeague, players);
    leagueRepository.save(league);

    createAndAssignLeagueRoles(players, league);
    assignAuthoritiesForPlayers(players);
  }

  private List<Player> buildPlayersList(final XmlLeague xmlLeague) {
    final Set<XmlPlayer> xmlPlayers = new LinkedHashSet<>();

    for (final XmlSeason season : xmlLeague.getSeasons()) {
      for (final XmlRound round : season.getRounds()) {
        for (final XmlGroup group : round.getGroups()) {
          xmlPlayers.addAll(group.getPlayers());
        }
      }
    }

    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);

    final List<Player> players = new ArrayList<>();
    for (final XmlPlayer xmlPlayer : xmlPlayers) {
      final String username = xmlPlayer.getName();
      final String email = xmlPlayer.getName().replace(" ", "_").toLowerCase() + "@gmail.com";
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

  private League buildLeague(final XmlLeague xmlLeague, final List<Player> players) {
    final League league = FromXmlConstructUtil.constructLeague(xmlLeague);

    for (final XmlHallOfFameSeason xmlHallOfFameSeason : xmlLeague.getHallOfFameSeasons()) {
      final HallOfFameSeason hallOfFameSeason = FromXmlConstructUtil.constructHallOfFameSeason(xmlHallOfFameSeason);
      league.addHallOfFameSeason(hallOfFameSeason);
    }

    for (final XmlSeason xmlSeason : xmlLeague.getSeasons()) {
      final Season season = FromXmlConstructUtil.constructSeason(xmlSeason);

      for (final XmlBonus xmlBonus : xmlSeason.getBonusPoints()) {
        final BonusPoint bonusPoint = FromXmlConstructUtil.constructBonusPoints(xmlBonus, players);
        season.addBonusPoint(bonusPoint);
      }

      for (final XmlRound xmlRound : xmlSeason.getRounds()) {
        final Round round = FromXmlConstructUtil.constructRound(xmlRound);

        for (final XmlGroup xmlGroup : xmlRound.getGroups()) {
          final RoundGroup roundGroup = FromXmlConstructUtil.constructRoundGroup(xmlGroup);

          int matchNumber = 1;
          for (final XmlMatch xmlMatch : xmlGroup.getMatches()) {
            final Match match = FromXmlConstructUtil.constructMatch(xmlMatch, players);
            match.setNumber(matchNumber++);

            for (int i = 0; i < xmlMatch.getSets().size(); i++) {
              final int setNumber = i + 1;
              final XmlSet xmlSet = xmlMatch.getSets().get(i);
              final SetResult setResult = FromXmlConstructUtil.constructSetResult(setNumber, xmlSet);
              match.addSetResult(setResult);
            }

            if (xmlMatch.getSets().size() == 2) {
              final SetResult setResult = FromXmlConstructUtil.constructEmptySetResult(3);
              match.addSetResult(setResult);
            }

            roundGroup.addMatch(match);
          }
          round.addRoundGroup(roundGroup);
        }
        FromXmlConstructUtil.setSplitForRound(round);
        season.addRound(round);
      }
      league.addSeason(season);
    }
    return league;
  }

  private void createAndAssignLeagueRoles(final List<Player> players, final League league) {
    final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
    league.addRoleForLeague(playerRole);

    final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
    league.addRoleForLeague(moderatorRole);

    for (final Player player : players) {
      player.addRole(playerRole);
      if (Arrays.asList("Maniak", "Dziad", "Siwy").contains(player.getUsername())) {
        player.addRole(moderatorRole);
      }
    }

    roleForLeagueRepository.save(playerRole);
    roleForLeagueRepository.save(moderatorRole);
  }

  private void assignAuthoritiesForPlayers(final List<Player> players) {
    final Authority adminAuthority = authorityRepository.findByType(AuthorityType.ROLE_ADMIN);
    final Authority userAuthority = authorityRepository.findByType(AuthorityType.ROLE_USER);

    for (final Player player : players) {
      player.addAuthority(userAuthority);
      if (player.getUsername().equals("Maniak")) {
        player.addAuthority(adminAuthority);
      }
    }

    authorityRepository.save(adminAuthority);
    authorityRepository.save(userAuthority);
  }

}
