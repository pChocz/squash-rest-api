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

import java.util.Comparator;
import java.util.List;
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
  private BonusPointService bonusPointService;

  @Autowired
  private XpPointsService xpPointsService;

  public SeasonScoreboardDto overalScoreboard(final Long seasonId) {
    final List<SetResult> setResultListForSeason = setResultRepository.fetchBySeasonId(seasonId);
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

    seasonScoreboardDto.sortRows();
    return seasonScoreboardDto;
  }

}
