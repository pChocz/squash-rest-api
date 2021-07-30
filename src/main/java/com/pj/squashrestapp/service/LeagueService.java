package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForLeague;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.PlayerLeagueXpOveral;
import com.pj.squashrestapp.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.dto.leaguestats.PerSeasonStats;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.match.SetDto;
import com.pj.squashrestapp.dto.scoreboard.EntireLeagueScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueLogo;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.LeagueRule;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.LeagueLogoRepository;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.LeagueRulesRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.TrophiesForLeagueRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import com.pj.squashrestapp.util.RomanUtil;
import com.pj.squashrestapp.util.RoundingUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueService {

  private final XpPointsService xpPointsService;
  private final BonusPointService bonusPointService;
  private final SeasonService seasonService;
  private final DeepRemovalService deepRemovalService;

  private final LeagueRepository leagueRepository;
  private final LeagueLogoRepository leagueLogoRepository;
  private final LeagueRulesRepository leagueRulesRepository;
  private final SeasonRepository seasonRepository;
  private final AdditionalMatchRepository additionalMatchRepository;
  private final PlayerRepository playerRepository;
  private final RoleForLeagueRepository roleForLeagueRepository;
  private final SetResultRepository setResultRepository;
  private final TrophiesForLeagueRepository trophiesForLeagueRepository;

  /**
   * This method creates the league itself as well as both roles (PLAYER, MODERATOR) that can be
   * assigned to players later.
   *
   * <p>Player that is requesting the league to be created will be automatically assigned as both
   * PLAYER and MODERATOR.
   */
  public UUID createNewLeague(
      final String leagueName,
      final String logoBase64,
      final int numberOfRounds,
      final int numberOfRoundsToBeDeducted,
      final MatchFormatType matchFormatType,
      final SetWinningType regularSetWinningType,
      final int regularSetWinningPoints,
      final SetWinningType tiebreakWinningType,
      final int tiebreakWinningPoints,
      final String leagueWhen,
      final String leagueWhere) {

    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player =
        playerRepository
            .fetchForAuthorizationByUsernameOrEmailUppercase(auth.getName().toUpperCase())
            .orElseThrow();

    final League league = new League(leagueName);
    league.setDateOfCreation(LocalDateTime.now());
    league.setNumberOfRoundsPerSeason(numberOfRounds);
    league.setRoundsToBeDeducted(numberOfRoundsToBeDeducted);
    league.setMatchFormatType(matchFormatType);
    league.setRegularSetWinningType(regularSetWinningType);
    league.setRegularSetWinningPoints(regularSetWinningPoints);
    league.setTiebreakWinningType(tiebreakWinningType);
    league.setTiebreakWinningPoints(tiebreakWinningPoints);
    if (leagueWhen != null) {
      league.setTime(leagueWhen);
    }
    if (leagueWhere != null) {
      league.setLocation(leagueWhere);
    }

    final byte[] logoBytes = Base64.getUrlDecoder().decode(logoBase64);

    final LeagueLogo leagueLogo = new LeagueLogo();
    leagueLogo.setPicture(logoBytes);
    league.setLeagueLogo(leagueLogo);

    final RoleForLeague playerRole = new RoleForLeague(LeagueRole.PLAYER);
    final RoleForLeague moderatorRole = new RoleForLeague(LeagueRole.MODERATOR);
    league.addRoleForLeague(playerRole);
    league.addRoleForLeague(moderatorRole);

    player.addRole(playerRole);
    player.addRole(moderatorRole);

    playerRepository.save(player);
    leagueRepository.save(league);
    roleForLeagueRepository.save(playerRole);
    roleForLeagueRepository.save(moderatorRole);

    return league.getUuid();
  }

  /**
   * Performs complete removal of a league from the DB, including all matches and unassigns all
   * players roles.
   *
   * @param leagueUuid UUID of a league to remove
   */
  public void removeLeague(final UUID leagueUuid) {
    final League leagueToRemove = leagueRepository.findByUuid(leagueUuid).orElseThrow();

    // player roles
    final List<Player> leaguePlayers = playerRepository.fetchForAuthorizationForLeague(leagueUuid);
    for (final Player player : leaguePlayers) {
      player.getRoles().removeIf(roleForLeague -> roleForLeague.getLeague().equals(leagueToRemove));
    }
    playerRepository.saveAll(leaguePlayers);

    // roles for league
    final List<RoleForLeague> rolesForLeague = roleForLeagueRepository.findByLeague(leagueToRemove);
    roleForLeagueRepository.deleteAll(rolesForLeague);

    // league rules
    final List<LeagueRule> leagueRules =
        leagueRulesRepository.findAllByLeagueOrderByOrderValueAsc(leagueToRemove);
    leagueRulesRepository.deleteAll(leagueRules);

    // logo
    final Optional<LeagueLogo> logoOptional = leagueLogoRepository.findByLeague(leagueToRemove);
    logoOptional.ifPresent(leagueLogoRepository::delete);

    // deep removal of:
    // - additional matches
    // - round matches / roundgroups / rounds / seasons
    // - bonus points
    // - trophies
    deepRemovalService.deepRemoveLeague(leagueUuid);
  }

  public void changeLogoForLeague(final UUID leagueUuid, final String logoBase64) {
    final byte[] logoBytes = Base64.getUrlDecoder().decode(logoBase64);

    final LeagueLogo leagueLogo = new LeagueLogo();
    leagueLogo.setPicture(logoBytes);

    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    league.setLeagueLogo(leagueLogo);
    leagueLogo.setLeague(league);

    leagueLogoRepository.save(leagueLogo);
  }

  public LeagueStatsWrapper buildStatsForLeagueUuid(final UUID leagueUuid) {
    final List<SetResult> setResultListForLeague =
        setResultRepository.fetchByLeagueUuid(leagueUuid);
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final League leagueReconstructed =
        EntityGraphBuildUtil.reconstructLeague(setResultListForLeague, league.getId());

    if (setResultListForLeague.isEmpty()) {
      return new LeagueStatsWrapper(
          league.getName(),
          league.getUuid(),
          new ArrayList<>(),
          new EntireLeagueScoreboard(league));
    }

    final ArrayListMultimap<String, Integer> xpPointsPerSplit =
        xpPointsService.buildAllAsIntegerMultimap();

    // per season stats
    final List<PerSeasonStats> perSeasonStatsList = buildPerSeasonStatsList(leagueReconstructed);

    // per player scoreboards
    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList =
        overalXpPoints(leagueReconstructed, xpPointsPerSplit);
    final EntireLeagueScoreboard scoreboard =
        new EntireLeagueScoreboard(leagueReconstructed, playerLeagueXpOveralList);

    return LeagueStatsWrapper.builder()
        .leagueName(leagueReconstructed.getName())
        .leagueUuid(leagueReconstructed.getUuid())
        .perSeasonStats(perSeasonStatsList)
        .scoreboard(scoreboard)
        .build();
  }

  private List<PerSeasonStats> buildPerSeasonStatsList(final League league) {
    if (league.getSeasons().isEmpty()) {
      return new ArrayList<>();
    }

    final List<PerSeasonStats> perSeasonStatsList = new ArrayList<>();

    final List<Season> seasonsReversedOrder =
        league.getSeasons().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

    for (final Season season : seasonsReversedOrder) {
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
      final BigDecimal tieBreakMatchesPercentsRounded =
          RoundingUtil.round(tieBreakMatchesPercents, 1);

      final float playersAverage = (float) playersAttendicesMap.size() / season.getRounds().size();
      final BigDecimal playersAverageRounded = RoundingUtil.round(playersAverage, 1);

      perSeasonStatsList.add(
          PerSeasonStats.builder()
              .seasonNumber(season.getNumber())
              .seasonNumberRoman(RomanUtil.toRoman(season.getNumber()))
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

    return perSeasonStatsList;
  }

  public List<PlayerLeagueXpOveral> overalXpPoints(
      final League league, final ArrayListMultimap<String, Integer> xpPointsPerSplit) {

    final BonusPointsAggregatedForLeague bonusPointsAggregatedForLeague =
        bonusPointService.extractBonusPointsAggregatedForLeague(league.getUuid());

    final List<SeasonScoreboardDto> seasonScoreboardDtoList = new ArrayList<>();
    for (final Season season : league.getSeasons()) {
      final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason =
          bonusPointsAggregatedForLeague.forSeason(season.getUuid());
      final SeasonScoreboardDto scoreboardDto =
          seasonService.getSeasonScoreboardDtoForLeagueStats(
              season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
      seasonScoreboardDtoList.add(scoreboardDto);
    }

    final ArrayListMultimap<PlayerDto, SeasonScoreboardRowDto> playersMap =
        ArrayListMultimap.create();
    for (final SeasonScoreboardDto seasonScoreboardDto : seasonScoreboardDtoList) {
      for (final SeasonScoreboardRowDto seasonScoreboardRowDto :
          seasonScoreboardDto.getSeasonScoreboardRows()) {
        playersMap.put(seasonScoreboardRowDto.getPlayer(), seasonScoreboardRowDto);
      }
    }

    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList =
        playersMap.keySet().stream()
            .map(playerDto -> new PlayerLeagueXpOveral(playersMap.get(playerDto)))
            .sorted(Comparator.comparingInt(PlayerLeagueXpOveral::getTotalPoints).reversed())
            .collect(Collectors.toList());

    return playerLeagueXpOveralList;
  }

  @Transactional
  public LeagueDto buildGeneralInfoForLeague(final UUID leagueUuid) {
    final League league =
        leagueRepository
            .findByUuid(leagueUuid)
            .orElseThrow(() -> new NoSuchElementException(ErrorCode.LEAGUE_NOT_FOUND));

    final LeagueDto leagueDto = new LeagueDto(league);

    final LeagueLogo leagueLogo = league.getLeagueLogo();
    if (leagueLogo != null) {
      leagueDto.setLeagueLogo(leagueLogo.getPicture());
    }

    return leagueDto;
  }

  public List<LeagueDto> buildGeneralInfoForAllLeagues() {
    final List<League> leagues = leagueRepository.findAllGeneralInfo();
    final List<LeagueDto> leaguesDtos =
        leagues.stream().map(LeagueDto::new).collect(Collectors.toList());
    return leaguesDtos;
  }

  public List<PlayerDto> extractLeaguePlayersGeneral(final UUID leagueUuid) {
    final List<Player> players =
        playerRepository.fetchGeneralInfoSorted(
            leagueUuid, Sort.by(Sort.Direction.ASC, "username"));

    final List<PlayerDto> playersDtos =
        players.stream().map(PlayerDto::new).collect(Collectors.toList());

    return playersDtos;
  }

  public Map<UUID, byte[]> extractAllLogos() {
    final Map<UUID, byte[]> leagueLogosMap = new HashMap<>();

    final List<League> leagues = leagueRepository.findAllRaw();
    final List<LeagueLogo> leagueLogos = leagueLogoRepository.findAll();

    for (final League league : leagues) {
      final UUID uuid = league.getUuid();
      leagueLogos.stream()
          .filter(logo -> logo.getLeague().getUuid().equals(uuid))
          .findFirst()
          .ifPresent(logo -> leagueLogosMap.put(uuid, logo.getPicture()));
    }

    return leagueLogosMap;
  }

  public OveralStats buildOveralStatsForLeagueUuid(final UUID leagueUuid) {
    final League league =
        leagueRepository
            .findByUuid(leagueUuid)
            .orElseThrow(() -> new NoSuchElementException(ErrorCode.LEAGUE_NOT_FOUND));

    final List<Long> playersIdsFirstPlayerForLeagueByUuid =
        leagueRepository.findPlayersIdsFirstPlayerForLeagueByUuid(leagueUuid);
    final List<Long> playersIdsSecondPlayerForLeagueByUuid =
        leagueRepository.findPlayersIdsSecondPlayerForLeagueByUuid(leagueUuid);
    final HashSet<Long> playersIds = new HashSet<>();
    playersIds.addAll(playersIdsFirstPlayerForLeagueByUuid);
    playersIds.addAll(playersIdsSecondPlayerForLeagueByUuid);
    final int allPlayers = playersIds.size();

    final Object[] counts = (Object[]) leagueRepository.findAllCountsForLeagueByUuid(leagueUuid);
    final Long numberOfSeasons = (Long) counts[0];
    final Long numberOfRounds = (Long) counts[1];
    final Long numberOfMatches = (Long) counts[2];
    final Long numberOfSets = (Long) counts[3];
    final Long numberOfRallies = counts[4] == null ? 0 : (Long) counts[4];

    final List<Object> groupedPlayersForLeagueByUuid =
        leagueRepository.findRoundsPerSplitGroupedForLeagueByUuid(leagueUuid);
    int countOfAttendices = 0;
    int countOfGroups = 0;

    for (final Object object : groupedPlayersForLeagueByUuid) {
      final Object[] group = (Object[]) object;
      final String split = (String) group[0];
      final int count = ((Long) group[1]).intValue();
      final int[] splitAsArray =
          Arrays.stream(split.split("\\|")).map(String::trim).mapToInt(Integer::valueOf).toArray();
      final int groupsPerRound = splitAsArray.length;
      final int playersPerRound = Arrays.stream(splitAsArray).sum();
      countOfGroups += groupsPerRound * count;
      countOfAttendices += playersPerRound * count;
    }

    final BigDecimal averagePlayersPerRoundRounded;
    final BigDecimal averagePlayersPerGroupRounded;
    final BigDecimal averageGroupsPerRoundRounded;

    if (numberOfRounds == 0) {
      averagePlayersPerRoundRounded = BigDecimal.valueOf(0);
      averagePlayersPerGroupRounded = BigDecimal.valueOf(0);
      averageGroupsPerRoundRounded = BigDecimal.valueOf(0);

    } else {
      final float averagePlayersPerRound = (float) countOfAttendices / numberOfRounds;
      averagePlayersPerRoundRounded = RoundingUtil.round(averagePlayersPerRound, 1);
      final float averagePlayersPerGroup = (float) countOfAttendices / countOfGroups;
      averagePlayersPerGroupRounded = RoundingUtil.round(averagePlayersPerGroup, 1);
      final float averageGroupsPerRound = (float) countOfGroups / numberOfRounds;
      averageGroupsPerRoundRounded = RoundingUtil.round(averageGroupsPerRound, 1);
    }

    final OveralStats overalStats =
        OveralStats.builder()
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
            .matchFormatType(league.getMatchFormatType())
            .regularSetWinningType(league.getRegularSetWinningType())
            .tiebreakWinningType(league.getTiebreakWinningType())
            .regularSetWinningPoints(league.getRegularSetWinningPoints())
            .tiebreakWinningPoints(league.getTiebreakWinningPoints())
            .numberOfRoundsPerSeason(league.getNumberOfRoundsPerSeason())
            .roundsToBeDeducted(league.getRoundsToBeDeducted())
            .dateOfCreation(LocalDate.from(league.getDateOfCreation()))
            .build();

    return overalStats;
  }

  public boolean checkLeagueNameTaken(final String leagueName) {
    final String leagueNameTrimmed = leagueName.trim();
    final List<League> leagues = leagueRepository.findAllRaw();
    final boolean isTaken = leagues.stream().anyMatch(leagueNameEqualPredicate(leagueNameTrimmed));
    return isTaken;
  }

  private Predicate<League> leagueNameEqualPredicate(final String leagueNameTrimmed) {
    return league -> league.getName().trim().equalsIgnoreCase(leagueNameTrimmed);
  }
}
