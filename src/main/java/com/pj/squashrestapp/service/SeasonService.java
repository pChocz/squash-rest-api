package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.dbinit.service.BackupService;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.dto.scoreboard.RoundAndGroupPosition;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.dto.scoreboard.SeasonStar;
import com.pj.squashrestapp.dto.scoreboard.Type;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeasonService {

  private final BonusPointService bonusPointService;
  private final XpPointsService xpPointsService;
  private final BackupService backupService;
  private final RedisCacheService redisCacheService;

  private final SetResultRepository setResultRepository;
  private final PlayerRepository playerRepository;
  private final SeasonRepository seasonRepository;
  private final LeagueRepository leagueRepository;

  public SeasonScoreboardDto getSeasonScoreboardDtoForLeagueStats(
      final Season season,
      final ArrayListMultimap<String, Integer> xpPointsPerSplit,
      final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {
    final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season);
    return buildSeasonScoreboardDto(
        seasonScoreboardDto, season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
  }

  public UUID extractLeagueUuid(final UUID seasonUuid) {
    return seasonRepository.retrieveLeagueUuidOfSeason(seasonUuid);
  }

  public SeasonScoreboardDto buildSeasonScoreboardDto(
      final SeasonScoreboardDto seasonScoreboardDto,
      final Season season,
      final ArrayListMultimap<String, Integer> xpPointsPerSplit,
      final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {

    for (final Round round : season.getFinishedRoundsOrdered()) {
      // remove uber-star immediately (as it's valid for 1 round only)
      seasonScoreboardDto.getSeasonStars().values().removeIf(star -> star.getType() == Type.UBER);

      final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
      for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
        roundScoreboard.addRoundGroupNew(roundGroup);
      }

      final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
      final String split = GeneralUtil.integerListToString(playersPerGroup);
      final List<Integer> xpPoints = xpPointsPerSplit.get(split + "|" + season.getXpPointsType());
      roundScoreboard.assignPointsAndPlaces(xpPoints);

      for (final RoundGroupScoreboard scoreboard : roundScoreboard.getRoundGroupScoreboards()) {
        for (final RoundGroupScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {
          final PlayerDto player = scoreboardRow.getPlayer();
          final SeasonScoreboardRowDto seasonScoreboardRowDto =
              seasonScoreboardDto.getSeasonScoreboardRows().stream()
                  .filter(p -> p.getPlayer().equals(player))
                  .findFirst()
                  .orElse(new SeasonScoreboardRowDto(player, bonusPointsAggregatedForSeason));

          seasonScoreboardRowDto.addScoreboardRow(scoreboardRow);

          // if it's not the first group, count pretenders points as well
          if (scoreboardRow.getPlaceInGroup() != scoreboardRow.getPlaceInRound()) {
            seasonScoreboardRowDto.addXpForRoundPretendents(
                round.getNumber(), scoreboardRow.getXpEarned());
          }

          // removing stars for each player that has played in that group
          seasonScoreboardDto.getSeasonStars().remove(player.getUuid());

          final int roundNumber = round.getNumber();
          final int groupNumber = scoreboard.getRoundGroupNumber();
          final int placeInGroup = scoreboardRow.getPlaceInGroup();
          final int placeInRound = scoreboardRow.getPlaceInRound();
          final boolean isFirstPlace = placeInGroup == 1;
          final boolean isLastPlace =
              scoreboard.getScoreboardRows().indexOf(scoreboardRow)
                  == scoreboard.getScoreboardRows().size() - 1;

          // add stars for first and last places
          if (isFirstPlace && groupNumber == 1) {
            final SeasonStar seasonStar =
                new SeasonStar(
                    roundNumber, String.valueOf((char) (groupNumber + 'A' - 1)), Type.UBER);
            seasonScoreboardDto.getSeasonStars().put(player.getUuid(), seasonStar);
          } else if (isFirstPlace) {
            final SeasonStar seasonStar =
                new SeasonStar(
                    roundNumber, String.valueOf((char) (groupNumber + 'A' - 2)), Type.PROMOTION);
            seasonScoreboardDto.getSeasonStars().put(player.getUuid(), seasonStar);
          } else if (isLastPlace) {
            final SeasonStar seasonStar =
                new SeasonStar(
                    roundNumber, String.valueOf((char) (groupNumber + 'A')), Type.RELEGATION);
            seasonScoreboardDto.getSeasonStars().put(player.getUuid(), seasonStar);
          }

          seasonScoreboardRowDto.addXpForRound(
              round.getNumber(),
              new RoundAndGroupPosition(
                  String.valueOf((char) (groupNumber + 'A' - 1)),
                  placeInGroup,
                  placeInRound,
                  scoreboardRow.getXpEarned(),
                  isLastPlace));
          final boolean containsPlayer =
              seasonScoreboardDto.getSeasonScoreboardRows().contains(seasonScoreboardRowDto);
          if (!containsPlayer) {
            seasonScoreboardDto.getSeasonScoreboardRows().add(seasonScoreboardRowDto);
          }
        }
      }
      // remove all stars if it's the last round of the season
      final boolean isSeasonFinished = round.getNumber() == seasonScoreboardDto.getAllRounds();
      if (isSeasonFinished) {
        seasonScoreboardDto.getSeasonStars().clear();
      }
    }

    for (final SeasonScoreboardRowDto seasonScoreboardRowDto :
        seasonScoreboardDto.getSeasonScoreboardRows()) {
      seasonScoreboardRowDto.calculateFinishedRow(seasonScoreboardDto.getCountedRounds());
    }

    seasonScoreboardDto.sortByCountedPoints();
    return seasonScoreboardDto;
  }

  public SeasonScoreboardDto extractLeaguePlayersSortedByPointsInSeason(final UUID seasonUuid) {
    // first - get all the players that have already played in the season (need to extract entire
    // season scoreboard)
    final SeasonScoreboardDto currentSeasonScoreboardDto = overalScoreboard(seasonUuid);

    final SeasonScoreboardDto seasonScoreboardDto;
    if (currentSeasonScoreboardDto.getFinishedRounds() == 0
        && currentSeasonScoreboardDto.previousSeasonExists()) {
      seasonScoreboardDto = overalScoreboard(currentSeasonScoreboardDto.getPreviousSeasonUuid());
      seasonScoreboardDto.sortByCountedPoints();

    } else {
      seasonScoreboardDto = overalScoreboard(seasonUuid);
      seasonScoreboardDto.sortByTotalPoints();
    }

    final List<PlayerDto> seasonPlayersSorted =
        seasonScoreboardDto.getSeasonScoreboardRows().stream()
            .map(SeasonScoreboardRowDto::getPlayer)
            .collect(Collectors.toList());

    // second - get all the players from entire League
    final UUID leagueUuid = seasonScoreboardDto.getSeason().getLeagueUuid();
    final List<Player> leaguePlayers =
        playerRepository.fetchGeneralInfoSorted(
            leagueUuid, Sort.by(Sort.Direction.ASC, "username"));
    final List<PlayerDto> leaguePlayersDtos =
        leaguePlayers.stream().map(PlayerDto::new).collect(Collectors.toList());

    for (final PlayerDto player : leaguePlayersDtos) {
      if (!seasonPlayersSorted.contains(player)) {
        seasonPlayersSorted.add(player);
        seasonScoreboardDto
            .getSeasonScoreboardRows()
            .add(
                new SeasonScoreboardRowDto(
                    player, new BonusPointsAggregatedForSeason(seasonUuid, new ArrayList<>())));
      }
    }

    return seasonScoreboardDto;
  }

  @Cacheable(value = RedisCacheConfig.SEASON_SCOREBOARD_CACHE, key = "#seasonUuid")
  public SeasonScoreboardDto overalScoreboard(final UUID seasonUuid) {
    final SeasonScoreboardDto seasonScoreboardDto = buildSeasonScoreboardDto(seasonUuid);
    return seasonScoreboardDto;
  }

  public SeasonScoreboardDto buildSeasonScoreboardDto(final UUID seasonUuid) {
    final List<SetResult> setResultListForSeason =
        setResultRepository.fetchBySeasonUuid(seasonUuid);
    final Long seasonId = seasonRepository.findIdByUuid(seasonUuid);

    Season season = EntityGraphBuildUtil.reconstructSeason(setResultListForSeason, seasonId);
    if (season == null) {
      season =
          seasonRepository
              .findSeasonByUuid(seasonUuid)
              .orElseThrow(() -> new NoSuchElementException("Season does not exist!"));
    }

    final ArrayListMultimap<String, Integer> xpPointsPerSplit =
        xpPointsService.buildAllAsIntegerMultimap();

    final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason =
        bonusPointService.extractBonusPointsAggregatedForSeason(seasonUuid);

    final SeasonScoreboardDto seasonScoreboardDto =
        getSeasonScoreboardDto(season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
    return seasonScoreboardDto;
  }

  public SeasonScoreboardDto getSeasonScoreboardDto(
      final Season season,
      final ArrayListMultimap<String, Integer> xpPointsPerSplit,
      final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {

    final int currentSeasonNumber = season.getNumber();
    final League currentLeague = season.getLeague();

    final UUID previousSeasonUuid =
        seasonRepository
            .findByLeagueAndNumber(currentLeague, currentSeasonNumber - 1)
            .map(Season::getUuid)
            .orElse(null);

    final UUID nextSeasonUuid =
        seasonRepository
            .findByLeagueAndNumber(currentLeague, currentSeasonNumber + 1)
            .map(Season::getUuid)
            .orElse(null);

    final SeasonScoreboardDto seasonScoreboardDto =
        new SeasonScoreboardDto(season, previousSeasonUuid, nextSeasonUuid);

    return buildSeasonScoreboardDto(
        seasonScoreboardDto, season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
  }

  public UUID getCurrentSeasonUuidForLeague(final UUID leagueUuid) {
    final List<Season> currentSeasonAsList =
        seasonRepository.findCurrentSeasonForLeague(leagueUuid, PageRequest.of(0, 1));
    if (currentSeasonAsList.isEmpty()) {
      return null;
    } else {
      return currentSeasonAsList.get(0).getUuid();
    }
  }

  @Transactional
  public SeasonDto extractSeasonDtoByUuid(final UUID seasonUuid) {
    final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
    final SeasonDto seasonDto = new SeasonDto(season);
    return seasonDto;
  }

  public List<PlayerDto> extractSeasonPlayers(final UUID seasonUuid) {
    final Set<PlayerDto> playersFirst =
        seasonRepository.extractSeasonPlayersFirst(seasonUuid).stream()
            .map(PlayerDto::new)
            .collect(Collectors.toSet());

    final Set<PlayerDto> playersSecond =
        seasonRepository.extractSeasonPlayersSecond(seasonUuid).stream()
            .map(PlayerDto::new)
            .collect(Collectors.toSet());

    final List<PlayerDto> merged =
        Stream.concat(playersFirst.stream(), playersSecond.stream())
            .distinct()
            .sorted(Comparator.comparing(PlayerDto::getUsername))
            .collect(Collectors.toList());

    return merged;
  }

  @Transactional
  public Season createNewSeason(
      final int seasonNumber,
      final LocalDate startDate,
      final UUID leagueUuid,
      final String xpPointsType,
      final String description) {
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final Season season = new Season(seasonNumber, startDate, xpPointsType);
    season.setNumberOfRounds(league.getNumberOfRoundsPerSeason());
    season.setRoundsToBeDeducted(league.getRoundsToBeDeducted());
    if (description != null) {
      season.setDescription(description);
    }
    league.addSeason(season);

    seasonRepository
        .findByLeagueAndNumber(league, season.getNumber() - 1)
        .ifPresent(redisCacheService::evictCacheForSeasonOnly);

    seasonRepository
        .findByLeagueAndNumber(league, season.getNumber() + 1)
        .ifPresent(redisCacheService::evictCacheForSeasonOnly);

    redisCacheService.evictCacheForSeason(season);
    leagueRepository.save(league);
    return season;
  }

  public void deleteSeason(final UUID seasonUuid) {
    final Season seasonToDelete = seasonRepository.findByUuidWithLeague(seasonUuid);

    seasonRepository
        .findByLeagueAndNumber(seasonToDelete.getLeague(), seasonToDelete.getNumber() - 1)
        .ifPresent(redisCacheService::evictCacheForSeasonOnly);

    seasonRepository
        .findByLeagueAndNumber(seasonToDelete.getLeague(), seasonToDelete.getNumber() + 1)
        .ifPresent(redisCacheService::evictCacheForSeasonOnly);

    redisCacheService.evictCacheForSeason(seasonToDelete);
    seasonRepository.delete(seasonToDelete);
  }
}
