package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.SeasonDto;
import com.pj.squashrestapp.model.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.ScoreboardRow;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class SeasonService {

  @Autowired
  private SetResultRepository setResultRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private BonusPointService bonusPointService;

  @Autowired
  private XpPointsService xpPointsService;

  @Autowired
  private SeasonRepository seasonRepository;

  public SeasonScoreboardDto overalScoreboard(final UUID seasonUuid) {
    final List<SetResult> setResultListForSeason = setResultRepository.fetchBySeasonId(seasonUuid);
    final Long seasonId = seasonRepository.findIdByUuid(seasonUuid);
    final Season season = EntityGraphBuildUtil.reconstructSeason(setResultListForSeason, seasonId);
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();

    final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason = bonusPointService.extractBonusPointsAggregatedForSeason(seasonId);

    final SeasonScoreboardDto seasonScoreboardDto = getSeasonScoreboardDto(season, xpPointsPerSplit, bonusPointsAggregatedForSeason);
    return seasonScoreboardDto;
  }

  public SeasonScoreboardDto getSeasonScoreboardDto(final Season season,
                                                    final ArrayListMultimap<String, Integer> xpPointsPerSplit,
                                                    final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason) {

    final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season);

    for (final Round round : season.getRoundsOrdered()) {
      final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
      for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
        roundScoreboard.addRoundGroupNew(roundGroup);
      }

      final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
      final String split = GeneralUtil.integerListToString(playersPerGroup);
      final List<Integer> xpPoints = xpPointsPerSplit.get(split);
      roundScoreboard.assignPointsAndPlaces(xpPoints);

      for (final RoundGroupScoreboard scoreboard : roundScoreboard.getRoundGroupScoreboards()) {
        for (final ScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {

          final PlayerDto player = scoreboardRow.getPlayer();
          final SeasonScoreboardRowDto seasonScoreboardRowDto = seasonScoreboardDto
                  .getSeasonScoreboardRows()
                  .stream()
                  .filter(p -> p.getPlayer().equals(player))
                  .findFirst()
                  .orElse(new SeasonScoreboardRowDto(player, bonusPointsAggregatedForSeason));

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

  @Transactional
  public SeasonDto extractSeasonDtoById(final Long seasonId) {
    final Season season = seasonRepository.findSeasonById(seasonId);
    final SeasonDto seasonDto = new SeasonDto(season);
    return seasonDto;
  }

}
