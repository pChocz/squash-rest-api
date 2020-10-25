package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.dbinit.jsondto.JsonSeason;
import com.pj.squashrestapp.dbinit.service.BackupService;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.SeasonDto;
import com.pj.squashrestapp.model.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.RoundGroupScoreboardRow;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
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
public class SeasonService {

  private final BonusPointService bonusPointService;
  private final XpPointsService xpPointsService;
  private final BackupService backupService;

  private final SetResultRepository setResultRepository;
  private final PlayerRepository playerRepository;
  private final SeasonRepository seasonRepository;
  private final LeagueRepository leagueRepository;


  public SeasonScoreboardDto getSeasonScoreboardDtoForLeagueStats(final Season season,
                                                                  final ArrayListMultimap<String, Integer> xpPointsPerSplit,
                                                                  final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {
    final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season);
    return buildSeasonScoreboardDto(seasonScoreboardDto, season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
  }

  public SeasonScoreboardDto buildSeasonScoreboardDto(final SeasonScoreboardDto seasonScoreboardDto,
                                                      final Season season,
                                                      final ArrayListMultimap<String, Integer> xpPointsPerSplit,
                                                      final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {

    for (final Round round : season.getFinishedRoundsOrdered()) {
      final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
      for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
        roundScoreboard.addRoundGroupNew(roundGroup);
      }

      final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
      final String split = GeneralUtil.integerListToString(playersPerGroup);
      final List<Integer> xpPoints = xpPointsPerSplit.get(split);
      roundScoreboard.assignPointsAndPlaces(xpPoints);

      for (final RoundGroupScoreboard scoreboard : roundScoreboard.getRoundGroupScoreboards()) {
        for (final RoundGroupScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {

          final PlayerDto player = scoreboardRow.getPlayer();
          final SeasonScoreboardRowDto seasonScoreboardRowDto = seasonScoreboardDto
                  .getSeasonScoreboardRows()
                  .stream()
                  .filter(p -> p.getPlayer().equals(player))
                  .findFirst()
                  .orElse(new SeasonScoreboardRowDto(player, bonusPointsAggregatedForSeason));

          seasonScoreboardRowDto.addScoreboardRow(scoreboardRow);

          // if it's not the first group, count pretenders points as well
          if (scoreboardRow.getPlaceInGroup() != scoreboardRow.getPlaceInRound()) {
            seasonScoreboardRowDto.addXpForRoundPretendents(round.getNumber(), scoreboardRow.getXpEarned());
          }

          seasonScoreboardRowDto.addXpForRound(round.getNumber(), scoreboardRow.getXpEarned());
          final boolean containsPlayer = seasonScoreboardDto.getSeasonScoreboardRows().contains(seasonScoreboardRowDto);
          if (!containsPlayer) {
            seasonScoreboardDto.getSeasonScoreboardRows().add(seasonScoreboardRowDto);
          }
        }
      }
    }

    for (final SeasonScoreboardRowDto seasonScoreboardRowDto : seasonScoreboardDto.getSeasonScoreboardRows()) {
      seasonScoreboardRowDto.calculateFinishedRow(seasonScoreboardDto.getFinishedRounds(), seasonScoreboardDto.getCountedRounds());
    }

    seasonScoreboardDto.sortByCountedPoints();
    return seasonScoreboardDto;
  }

  public List<PlayerDto> extractLeaguePlayersSortedByPointsInSeason(final UUID seasonUuid) {
    // first - get all the players that have already played in the season (need to extract entire season scoreboard)
    final SeasonScoreboardDto seasonScoreboardDto = overalScoreboard(seasonUuid);
    seasonScoreboardDto.sortByTotalPoints();
    final List<PlayerDto> seasonPlayersSorted = seasonScoreboardDto
            .getSeasonScoreboardRows()
            .stream()
            .map(SeasonScoreboardRowDto::getPlayer)
            .collect(Collectors.toList());

    // second - get all the players from entire League
    final UUID leagueUuid = seasonScoreboardDto.getSeason().getLeagueUuid();
    final List<Player> leaguePlayers = playerRepository.fetchGeneralInfoSorted(leagueUuid, Sort.by(Sort.Direction.ASC, "username"));
    final List<PlayerDto> leaguePlayersDtos = leaguePlayers
            .stream()
            .map(PlayerDto::new)
            .collect(Collectors.toList());

    for (final PlayerDto player : leaguePlayersDtos) {
      if (!seasonPlayersSorted.contains(player)) {
        seasonPlayersSorted.add(player);
      }
    }

    return seasonPlayersSorted;
  }

  public SeasonScoreboardDto overalScoreboard(final UUID seasonUuid) {
    final List<SetResult> setResultListForSeason = setResultRepository.fetchBySeasonUuid(seasonUuid);
    final Long seasonId = seasonRepository.findIdByUuid(seasonUuid);

    Season season = EntityGraphBuildUtil.reconstructSeason(setResultListForSeason, seasonId);
    if (season == null) {
      season = seasonRepository
              .findSeasonByUuid(seasonUuid)
              .orElseThrow(() -> new NoSuchElementException("Season does not exist!"));
    }

    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

    final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason = bonusPointService.extractBonusPointsAggregatedForSeason(seasonUuid);

    final SeasonScoreboardDto seasonScoreboardDto = getSeasonScoreboardDto(season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
    return seasonScoreboardDto;
  }

  public SeasonScoreboardDto getSeasonScoreboardDto(final Season season,
                                                    final ArrayListMultimap<String, Integer> xpPointsPerSplit,
                                                    final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {

    final int currentSeasonNumber = season.getNumber();
    final League currentLeague = season.getLeague();

    final UUID previousSeasonUuid = seasonRepository
            .findByLeagueAndNumber(currentLeague, currentSeasonNumber - 1)
            .map(Season::getUuid)
            .orElse(null);

    final UUID nextSeasonUuid = seasonRepository
            .findByLeagueAndNumber(currentLeague, currentSeasonNumber + 1)
            .map(Season::getUuid)
            .orElse(null);

    final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season, previousSeasonUuid, nextSeasonUuid);

    return buildSeasonScoreboardDto(seasonScoreboardDto, season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
  }

  @Transactional
  public SeasonDto extractSeasonDtoByUuid(final UUID seasonUuid) {
    final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
    final SeasonDto seasonDto = new SeasonDto(season);
    return seasonDto;
  }

  public List<PlayerDto> extractSeasonPlayers(final UUID seasonUuid) {
    final Set<PlayerDto> playersFirst = seasonRepository
            .extractSeasonPlayersFirst(seasonUuid)
            .stream()
            .map(PlayerDto::new)
            .collect(Collectors.toSet());

    final Set<PlayerDto> playersSecond = seasonRepository
            .extractSeasonPlayersSecond(seasonUuid)
            .stream()
            .map(PlayerDto::new)
            .collect(Collectors.toSet());

    final List<PlayerDto> merged = Stream
            .concat(
                    playersFirst.stream(),
                    playersSecond.stream())
            .distinct()
            .sorted(Comparator.comparing(PlayerDto::getUsername))
            .collect(Collectors.toList());

    return merged;
  }

  @Transactional
  public Season createNewSeason(final int seasonNumber, final LocalDate startDate, final UUID leagueUuid) {
    final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
    final Season season = new Season(seasonNumber, startDate);
    league.addSeason(season);
    leagueRepository.save(league);
    return season;
  }

  public void deleteSeason(final UUID seasonUuid) {
    final Season seasonToDelete = seasonRepository.findByUuid(seasonUuid).orElseThrow();

    final JsonSeason jsonSeason = backupService.seasonToJson(seasonUuid);
    final String seasonJsonContent = GsonUtil.gsonWithDate().toJson(jsonSeason);
    log.info("Removing season: \n{}", seasonJsonContent);

    seasonRepository.delete(seasonToDelete);
  }

}
