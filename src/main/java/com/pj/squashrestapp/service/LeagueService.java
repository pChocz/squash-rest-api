package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForLeague;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.PlayerLeagueXpOveral;
import com.pj.squashrestapp.dto.SetDto;
import com.pj.squashrestapp.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.dto.leaguestats.PerSeasonStats;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.scoreboard.EntireLeagueScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.LeagueLogoRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.TrophiesForLeagueRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import com.pj.squashrestapp.util.RoundingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueService {

  private final XpPointsService xpPointsService;
  private final BonusPointService bonusPointService;
  private final SeasonService seasonService;

  private final LeagueRepository leagueRepository;
  private final PlayerRepository playerRepository;
  private final LeagueLogoRepository leagueLogoRepository;
  private final RoleForLeagueRepository roleForLeagueRepository;
  private final SetResultRepository setResultRepository;
  private final TrophiesForLeagueRepository trophiesForLeagueRepository;

  /**
   * This method creates the league itself as well as both roles (USER, MODERATOR)
   * that can be assigned to the players later.
   *
   * @param leagueName name of the league to create
   * @return league DTO object
   */
  public LeagueDto createNewLeague(final String leagueName) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = playerRepository.fetchForAuthorizationByUsernameOrEmailUppercase(auth.getName().toUpperCase()).orElseThrow();

    final League league = new League(leagueName);

    final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
    final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
    league.addRoleForLeague(playerRole);
    league.addRoleForLeague(moderatorRole);

    player.addRole(moderatorRole);

    playerRepository.save(player);
    roleForLeagueRepository.save(playerRole);
    roleForLeagueRepository.save(moderatorRole);
    leagueRepository.save(league);

    return new LeagueDto(league);
  }

  public void removeEmptyLeague(final UUID leagueUuid) {
    final League leagueToRemove = leagueRepository
            .findByUuid(leagueUuid)
            .orElseThrow();

    final List<Player> leaguePlayers = playerRepository.fetchForAuthorizationForLeague(leagueUuid);
    for (final Player player : leaguePlayers) {
      player.getRoles()
              .removeIf(roleForLeague -> roleForLeague
                      .getLeague()
                      .equals(leagueToRemove));
    }
    playerRepository.saveAll(leaguePlayers);

    final List<RoleForLeague> rolesForLeague = roleForLeagueRepository.findByLeague(leagueToRemove);
    roleForLeagueRepository.deleteAll(rolesForLeague);

    leagueRepository.delete(leagueToRemove);
  }

  public void saveLogoForLeague(final UUID leagueUuid, final byte[] logoBytes) {
    final LeagueLogo leagueLogo = new LeagueLogo();
    leagueLogo.setPicture(logoBytes);

    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    league.setLeagueLogo(leagueLogo);
    leagueLogo.setLeague(league);

    leagueLogoRepository.save(leagueLogo);
  }


  public LeagueStatsWrapper buildStatsForLeagueUuid(final UUID leagueUuid) {
    final League league = fetchEntireLeague(leagueUuid);
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

    // per season stats
    final List<PerSeasonStats> perSeasonStatsList = buildPerSeasonStatsList(league);

    // per player scoreboards
    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList = overalXpPoints(league, xpPointsPerSplit);
    final EntireLeagueScoreboard scoreboard = new EntireLeagueScoreboard(league, playerLeagueXpOveralList);

    return LeagueStatsWrapper.builder()
            .leagueName(league.getName())
            .leagueUuid(league.getUuid())
            .perSeasonStats(perSeasonStatsList)
            .scoreboard(scoreboard)
            .build();
  }

  public League fetchEntireLeague(final UUID leagueUuid) {
    final List<SetResult> setResultListForLeague = setResultRepository.fetchByLeagueUuid(leagueUuid);
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    return EntityGraphBuildUtil.reconstructLeague(setResultListForLeague, league.getId());
  }

  private List<PerSeasonStats> buildPerSeasonStatsList(final League league) {
    final List<PerSeasonStats> perSeasonStatsList = new ArrayList<>();

    for (final Season season : league.getSeasons()) {
      final List<MatchDetailedDto> matchesForSeason = MatchExtractorUtil.extractAllMatches(season);

      int matches = 0;
      int tieBreaks = 0;
      int points = 0;

      final Multimap<UUID, UUID> playersAttendicesMap = LinkedHashMultimap.create();
      for (final MatchDetailedDto match : matchesForSeason) {
        matches++;
        playersAttendicesMap.put(match.getFirstPlayer().getUuid(), match.getRoundUuid());
        playersAttendicesMap.put(match.getSecondPlayer().getUuid(), match.getRoundUuid());
        for (final SetDto set : match.getSets()) {
          points += set.getFirstPlayerScoreNullSafe();
          points += set.getSecondPlayerScoreNullSafe();
          if (!set.isEmpty()) {
            if (set.isTieBreak()) {
              tieBreaks++;
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
              .seasonStartDate(season.getStartDate())
              .seasonUuid(season.getUuid())
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

    final BonusPointsAggregatedForLeague bonusPointsAggregatedForLeague = bonusPointService.extractBonusPointsAggregatedForLeague(league.getUuid());

    final List<SeasonScoreboardDto> seasonScoreboardDtoList = new ArrayList<>();
    for (final Season season : league.getSeasons()) {
      final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason = bonusPointsAggregatedForLeague.forSeason(season.getUuid());
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
    final List<League> leagues = leagueRepository.findAllGeneralInfo();
    final List<LeagueDto> leaguesDtos = leagues
            .stream()
            .map(LeagueDto::new)
            .collect(Collectors.toList());
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

  public Map<UUID, byte[]> extractAllLogos() {
    final Map<UUID, byte[]> leagueLogosMap = new HashMap<>();

    final List<League> leagues = leagueRepository.findAllRaw();
    final List<LeagueLogo> leagueLogos = leagueLogoRepository.findAll();

    for (final League league : leagues) {
      final UUID uuid = league.getUuid();
      leagueLogos
              .stream()
              .filter(logo -> logo.getLeague().getUuid().equals(uuid))
              .findFirst()
              .ifPresent(logo -> leagueLogosMap.put(uuid, logo.getPicture()));
    }

    return leagueLogosMap;
  }

  public OveralStats buildOveralStatsForLeagueUuid(final UUID leagueUuid) {
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();


    final List<Long> playersIdsFirstPlayerForLeagueByUuid = leagueRepository.findPlayersIdsFirstPlayerForLeagueByUuid(leagueUuid);
    final List<Long> playersIdsSecondPlayerForLeagueByUuid = leagueRepository.findPlayersIdsSecondPlayerForLeagueByUuid(leagueUuid);
    final HashSet<Long> playersIds = new HashSet<>();
    playersIds.addAll(playersIdsFirstPlayerForLeagueByUuid);
    playersIds.addAll(playersIdsSecondPlayerForLeagueByUuid);
    final int allPlayers = playersIds.size();


    final Object[] counts = (Object[]) leagueRepository.findAllCountsForLeagueByUuid(leagueUuid);
    final Long numberOfSeasons = (Long) counts[0];
    final Long numberOfRounds = (Long) counts[1];
    final Long numberOfMatches = (Long) counts[2];
    final Long numberOfSets = (Long) counts[3];
    final Long numberOfRallies = (Long) counts[4];


    final List<Object> groupedPlayersForLeagueByUuid = leagueRepository.findRoundsPerSplitGroupedForLeagueByUuid(leagueUuid);
    int countOfAttendices = 0;
    int countOfGroups = 0;
    for (final Object object : groupedPlayersForLeagueByUuid) {
      final Object[] group = (Object[]) object;
      final String split = (String) group[0];
      final int count = ((Long) group[1]).intValue();
      final int[] splitAsArray = Arrays
              .stream(split.split("\\|"))
              .map(String::trim)
              .mapToInt(Integer::valueOf)
              .toArray();
      final int groupsPerRound = splitAsArray.length;
      final int playersPerRound = Arrays.stream(splitAsArray).sum();
      countOfGroups += groupsPerRound * count;
      countOfAttendices += playersPerRound * count;
    }
    final float averagePlayersPerRound = (float) countOfAttendices / numberOfRounds;
    final BigDecimal averagePlayersPerRoundRounded = RoundingUtil.round(averagePlayersPerRound, 1);
    final float averagePlayersPerGroup = (float) countOfAttendices / countOfGroups;
    final BigDecimal averagePlayersPerGroupRounded = RoundingUtil.round(averagePlayersPerGroup, 1);
    final float averageGroupsPerRound = (float) countOfGroups / numberOfRounds;
    final BigDecimal averageGroupsPerRoundRounded = RoundingUtil.round(averageGroupsPerRound, 1);


    final OveralStats overalStats = OveralStats.builder()
            .leagueUuid(league.getUuid())
            .leagueName(league.getName())
            .location(league.getLocation())
            .time(league.getTime())
            .seasons(numberOfSeasons.intValue())
            .rounds(numberOfRounds.intValue())
            .matches(numberOfMatches.intValue())
            .sets(numberOfSets.intValue())
            .points(numberOfRallies.intValue())
            .players(allPlayers)
            .averagePlayersPerRound(averagePlayersPerRoundRounded)
            .averagePlayersPerGroup(averagePlayersPerGroupRounded)
            .averageGroupsPerRound(averageGroupsPerRoundRounded)
            .build();

    return overalStats;
  }

}
