package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.HallOfFameSeason;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.LeagueDto;
import com.pj.squashrestapp.model.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.PlayerLeagueXpOveral;
import com.pj.squashrestapp.model.dto.SetDto;
import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.model.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.model.dto.leaguestats.PerSeasonStats;
import com.pj.squashrestapp.model.dto.scoreboard.EntireLeagueScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.HallOfFameSeasonRepository;
import com.pj.squashrestapp.repository.LeagueLogoRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import com.pj.squashrestapp.util.RoundingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class LeagueService {

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private LeagueLogoRepository leagueLogoRepository;

  @Autowired
  private RoleForLeagueRepository roleForLeagueRepository;

  @Autowired
  private SetResultRepository setResultRepository;

  @Autowired
  private XpPointsService xpPointsService;

  @Autowired
  private BonusPointService bonusPointService;

  @Autowired
  private SeasonService seasonService;

  @Autowired
  private HallOfFameSeasonRepository hallOfFameSeasonRepository;

  /**
   * This method creates the league itself as well as both roles (USER, MODERATOR)
   * that can be assigned to the players later.
   *
   * @param leagueName name of the league to create
   * @param leagueModerator existing player that will be assigned as league moderator
   */
  public void createNewLeague(final String leagueName, final Player leagueModerator) {
    final League league = new League(leagueName);

    final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
    final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
    league.addRoleForLeague(playerRole);
    league.addRoleForLeague(moderatorRole);

    leagueModerator.addRole(moderatorRole);

    playerRepository.save(leagueModerator);
    roleForLeagueRepository.save(playerRole);
    roleForLeagueRepository.save(moderatorRole);
    leagueRepository.save(league);
  }

  public void removeEmptyLeague(final Long leagueId) {
    final League leagueToRemove = leagueRepository
            .findById(leagueId)
            .orElseThrow(() -> new RuntimeException("No such league"));

    final List<Player> leaguePlayers = playerRepository.fetchForAuthorizationForLeague(leagueId);
    for (final Player player : leaguePlayers) {
      player.getRoles()
              .removeIf(roleForLeague -> roleForLeague.getLeague().equals(leagueToRemove));
    }
    playerRepository.saveAll(leaguePlayers);

    final List<RoleForLeague> rolesForLeague = roleForLeagueRepository.findByLeague(leagueToRemove);
    roleForLeagueRepository.deleteAll(rolesForLeague);

    leagueRepository.delete(leagueToRemove);
  }

  public void saveLogoForLeague(final Long leagueId, final byte[] logoBytes) {
    final LeagueLogo leagueLogo = new LeagueLogo();
    leagueLogo.setPicture(logoBytes);

    final League league = leagueRepository.findById(leagueId).get();
    league.setLeagueLogo(leagueLogo);
    leagueLogo.setLeague(league);

    leagueLogoRepository.save(leagueLogo);
  }


  public LeagueStatsWrapper buildStatsForLeagueId(final UUID leagueUuid) {
    final League league = fetchEntireLeague(leagueUuid);
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

    // logo
    final byte[] logoBytes = leagueLogoRepository.extractLogoBlob(leagueUuid);

    // per season stats
    final List<PerSeasonStats> perSeasonStatsList = buildPerSeasonStatsList(league);

    // per player scoreboards
    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList = overalXpPoints(league, xpPointsPerSplit);
    final EntireLeagueScoreboard scoreboard = new EntireLeagueScoreboard(league, playerLeagueXpOveralList);

    // build overal stats
    final OveralStats overalStats = new OveralStats(perSeasonStatsList);

    // hall of fame
    final List<HallOfFameSeason> hallOfFame = hallOfFameSeasonRepository.findByLeague(league);

    return LeagueStatsWrapper.builder()
            .leagueName(league.getName())
            .logoBytes(logoBytes)
            .overalStats(overalStats)
            .perSeasonStats(perSeasonStatsList)
            .scoreboard(scoreboard)
            .hallOfFame(hallOfFame)
            .build();
  }

  public League fetchEntireLeague(final UUID leagueUuid) {
    final List<SetResult> setResultListForLeague = setResultRepository.fetchByLeagueId(leagueUuid);
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    return EntityGraphBuildUtil.reconstructLeague(setResultListForLeague, league.getId());
  }

//  private String extractLeagueLogo(final Blob blob) {
//    byte[] decodedBytes = new byte[0];
//    if (blob != null) {
//      try {
//        final int length = (int) blob.length();
//        final byte[] bytes = blob.getBytes(1, length);
//        decodedBytes = Base64.getDecoder().decode(bytes);
//        blob.free();
//      } catch (final SQLException e) {
//        log.error("SQL Exception when trying to encode league logo", e);
//      }
//    }
//
//    return Base64.getEncoder().encodeToString(decodedBytes);
//  }

  private List<PerSeasonStats> buildPerSeasonStatsList(final League league) {
    final List<PerSeasonStats> perSeasonStatsList = new ArrayList<>();

    for (final Season season : league.getSeasons()) {
      final List<MatchDetailedDto> matchesForSeason = MatchExtractorUtil.extractAllMatches(season);

      int matches = 0;
      int regularSets = 0;
      int tieBreaks = 0;
      int points = 0;

      final Multimap<Long, Long> playersAttendicesMap = LinkedHashMultimap.create();
      for (final MatchDetailedDto match : matchesForSeason) {
        matches++;
        playersAttendicesMap.put(match.getFirstPlayer().getId(), match.getRoundId());
        playersAttendicesMap.put(match.getSecondPlayer().getId(), match.getRoundId());
        for (final SetDto set : match.getSets()) {
          points += set.getFirstPlayerScoreNullSafe();
          points += set.getSecondPlayerScoreNullSafe();
          if (!set.isEmpty()) {
            if (set.isTieBreak()) {
              tieBreaks++;
            } else {
              regularSets++;
            }
          }
        }
      }

      final float tieBreakMatchesPercents = (float) 100 * tieBreaks / matches;
      final BigDecimal tieBreakMatchesPercentsRounded = RoundingUtil.round(tieBreakMatchesPercents, 1);

      final float playersAverage = (float) playersAttendicesMap.size() / season.getRounds().size();
      final BigDecimal playersAverageRounded = RoundingUtil.round(playersAverage, 1);

      perSeasonStatsList.add(PerSeasonStats.builder()
              .seasonNumber(season.getNumber())
              .rounds(season.getRounds().size())
              .regularMatches(matches - tieBreaks)
              .tieBreakMatches(tieBreaks)
              .tieBreakMatchesPercents(tieBreakMatchesPercentsRounded)
              .points(points)
              .playersAverage(playersAverageRounded)
              .players(playersAttendicesMap.keySet().size())
              .playersAttendicesMap(playersAttendicesMap)
              .build());
    }

    perSeasonStatsList.sort(Comparator.comparingInt(PerSeasonStats::getSeasonNumber));
    return perSeasonStatsList;
  }

  public List<PlayerLeagueXpOveral> overalXpPoints(final League league,
                                                   final ArrayListMultimap<String, Integer> xpPointsPerSplit) {

    final BonusPointsAggregatedForLeague bonusPointsAggregatedForLeague = bonusPointService.extractBonusPointsAggregatedForLeague(league.getId());

    final List<SeasonScoreboardDto> seasonScoreboardDtoList = new ArrayList<>();
    for (final Season season : league.getSeasons()) {
      final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason = bonusPointsAggregatedForLeague.forSeason(season.getId());
      final SeasonScoreboardDto scoreboardDto = seasonService.getSeasonScoreboardDtoForLeagueStats(season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
      seasonScoreboardDtoList.add(scoreboardDto);
    }

    final ArrayListMultimap<PlayerDto, SeasonScoreboardRowDto> playersMap = ArrayListMultimap.create();
    for (final SeasonScoreboardDto seasonScoreboardDto : seasonScoreboardDtoList) {
      for (final SeasonScoreboardRowDto seasonScoreboardRowDto : seasonScoreboardDto.getSeasonScoreboardRows()) {
        playersMap.put(seasonScoreboardRowDto.getPlayer(), seasonScoreboardRowDto);
      }
    }

    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList = playersMap
            .keySet()
            .stream()
            .map(playerDto -> new PlayerLeagueXpOveral(playersMap.get(playerDto)))
            .sorted(Comparator.comparingInt(PlayerLeagueXpOveral::getTotalPoints).reversed())
            .collect(Collectors.toList());

    return playerLeagueXpOveralList;
  }

  @Transactional
  public LeagueDto buildGeneralInfoForLeague(final UUID leagueUuid) {
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final LeagueDto leagueDto = new LeagueDto(league);

    final LeagueLogo leagueLogo = league.getLeagueLogo();
    if (leagueLogo != null) {
      leagueDto.setLeagueLogo(leagueLogo.getPicture());
    }

    return leagueDto;
  }

  public List<LeagueDto> buildGeneralInfoForAllLeagues() {
    final List<League> leagues = leagueRepository.findAll();
    final List<LeagueLogo> leagueLogos = leagueLogoRepository.findAll();
    final List<LeagueDto> leaguesDtos = leagues.stream().map(LeagueDto::new).collect(Collectors.toList());

    for (final LeagueDto leagueDto : leaguesDtos) {
      final LeagueLogo leagueLogo = leagueLogos.stream().filter(leagueLogo1 -> leagueLogo1.getLeague().getId().equals(leagueDto.getLeagueId())).findFirst().orElse(null);
      if (leagueLogo != null) {
        leagueDto.setLeagueLogo(leagueLogo.getPicture());
      }
    }

    return leaguesDtos;
  }

  public List<PlayerDto> extractLeaguePlayersGeneral(final UUID leagueUuid) {
    final List<Player> players = playerRepository.fetchGeneralInfoSorted(leagueUuid, Sort.by(Sort.Direction.ASC, "username"));

    final List<PlayerDto> playersDtos = players
            .stream()
            .map(PlayerDto::new)
            .collect(Collectors.toList());

    return playersDtos;
  }


}
