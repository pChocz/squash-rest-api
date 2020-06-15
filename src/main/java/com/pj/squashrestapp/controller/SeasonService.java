package com.pj.squashrestapp.controller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.RoundScoreboard;
import com.pj.squashrestapp.model.dto.Scoreboard;
import com.pj.squashrestapp.model.dto.ScoreboardRow;
import com.pj.squashrestapp.model.dto.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.SeasonScoreboardRowDto;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class SeasonService {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private XpPointsRepository xpPointsRepository;

  @Autowired
  private SeasonRepository seasonRepository;

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

    final AtomicLongMap<Long> bonusPointsAggregatedPerPlayer = AtomicLongMap.create();

    for (final BonusPoint bonusPoint : season.getBonusPoints()) {
      final Long playerId = bonusPoint.getPlayer().getId();
      final int points = bonusPoint.getPoints();
      bonusPointsAggregatedPerPlayer.getAndAdd(playerId, points);
    }

    final SeasonScoreboardDto seasonScoreboardDto = new SeasonScoreboardDto(season);

    for (final Round round : season.getRounds()) {
      final RoundScoreboard roundScoreboard = new RoundScoreboard();
      for (final RoundGroup roundGroup : round.getRoundGroups()) {
        roundScoreboard.addRoundGroupNew(roundGroup);
      }

      final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
      final String split = GeneralUtil.integerListToString(playersPerGroup);
      final List<Integer> xpPoints = xpPointsPerSplit.get(split);
      roundScoreboard.assignPointsAndPlaces(xpPoints);

      for (final Scoreboard scoreboard : roundScoreboard.getRoundGroupScoreboards()) {
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
      seasonScoreboardRowDto.calculateFinishedRow(seasonScoreboardDto.getFinishedRounds());
    }

    seasonScoreboardDto.sortRows();
    return seasonScoreboardDto;
  }

//  List<RoundScoreboard> perRoundScoreboard(final Long id) {
//    final List<Long> roundsIds = seasonRepository.retrieveFinishedGroupIdsBySeasonId(id);
//
//    final List<RoundScoreboard> roundScoreboards = new ArrayList<>();
//    for (final Long roundId : roundsIds) {
//      final List<SingleSetRowDto> sets = matchRepository.retrieveByRoundId(roundId);
//      final Multimap<Long, MatchDto> perGroupMatches = MatchUtil.rebuildRoundMatchesPerRoundGroupId(sets);
//
//      final RoundScoreboard roundScoreboard = new RoundScoreboard();
//      for (final Long roundGroupId : perGroupMatches.keySet()) {
//        roundScoreboard.addRoundGroup(perGroupMatches.get(roundGroupId));
//      }
//
//      final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
//      final String split = GeneralUtil.integerListToString(playersPerGroup);
//      final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplit(split);
//
//      roundScoreboard.assignPointsAndPlaces(xpPoints);
//      roundScoreboards.add(roundScoreboard);
//    }
//    return roundScoreboards;
//  }

}
