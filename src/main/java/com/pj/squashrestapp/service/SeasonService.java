package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.scoreboard.RoundGroupScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.ScoreboardRow;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class SeasonService {

  @Autowired
  private SetResultRepository setResultRepository;

  @Autowired
  private XpPointsService xpPointsService;

  public SeasonScoreboardDto overalScoreboard(final Long seasonId) {
    final List<SetResult> setResultListForSeason = setResultRepository.fetchBySeasonId(seasonId);
    final Season season = EntityGraphBuildUtil.reconstructSeason(setResultListForSeason, seasonId);
    final ArrayListMultimap<String, Integer> xpPointsPerSplit = xpPointsService.buildAllAsIntegerMultimap();
    final SeasonScoreboardDto seasonScoreboardDto = getSeasonScoreboardDto(season, xpPointsPerSplit);
    return seasonScoreboardDto;
  }

  public SeasonScoreboardDto getSeasonScoreboardDto(final Season season,
                                                    final ArrayListMultimap<String, Integer> xpPointsPerSplit) {

    final AtomicLongMap<Long> bonusPointsAggregatedPerPlayer = extractBonusPointsPerPlayer(season);

    final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season);

    for (final Round round : season.getRounds()) {
      final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
      for (final RoundGroup roundGroup : round.getRoundGroups()) {
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
                  .orElse(new SeasonScoreboardRowDto(player, bonusPointsAggregatedPerPlayer));

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

    seasonScoreboardDto.sortRows();
    return seasonScoreboardDto;
  }

  private AtomicLongMap<Long> extractBonusPointsPerPlayer(final Season season) {
    final AtomicLongMap<Long> bonusPointsAggregatedPerPlayer = AtomicLongMap.create();

    for (final BonusPoint bonusPoint : season.getBonusPoints()) {
      final Long playerId = bonusPoint.getPlayer().getId();
      final int points = bonusPoint.getPoints();
      bonusPointsAggregatedPerPlayer.getAndAdd(playerId, points);
    }
    return bonusPointsAggregatedPerPlayer;
  }

}
