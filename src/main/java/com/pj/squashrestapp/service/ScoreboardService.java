package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.RedisCacheConfig;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreboardService {

  private final RoundRepository roundRepository;
  private final SetResultRepository setResultRepository;
  private final XpPointsRepository xpPointsRepository;

  @Cacheable(value = RedisCacheConfig.ROUND_SCOREBOARD_CACHE, key = "#roundUuid")
  public RoundScoreboard buildScoreboardForRound(final UUID roundUuid) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);

    Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    if (round == null) {
      round =
          roundRepository
              .findByUuid(roundUuid)
              .orElseThrow(() -> new NoSuchElementException("Round does not exist!"));
    }

    final Season season = round.getSeason();

    final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
    for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
      roundScoreboard.addRoundGroupNew(roundGroup);
    }

    final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
    final String split = GeneralUtil.integerListToString(playersPerGroup);
    final String type = season.getXpPointsType();
    final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplitAndType(split, type);

    roundScoreboard.assignPointsAndPlaces(xpPoints);
    return roundScoreboard;
  }

  public UUID getCurrentSeasonUuidForPlayer(final UUID playerUuid) {
    final List<Round> mostRecentRoundAsList = roundRepository.findMostRecentRoundOfPlayer(playerUuid, PageRequest.of(0, 1));
    if (mostRecentRoundAsList.isEmpty()) {
      return null;
    } else {
      return mostRecentRoundAsList.get(0).getSeason().getUuid();
    }
  }

  public UUID getMostRecentRoundUuidForPlayer(final UUID playerUuid) {
    final List<Round> mostRecentRoundAsList = roundRepository.findMostRecentRoundOfPlayer(playerUuid, PageRequest.of(0, 1));
    if (mostRecentRoundAsList.isEmpty()) {
      return null;
    } else {
      return mostRecentRoundAsList.get(0).getUuid();
    }
  }

  public UUID getMostRecentRoundUuidForLeague(final UUID leagueUuid) {
    final List<Round> mostRecentRoundAsList = roundRepository.findMostRecentRoundOfLeague(leagueUuid, PageRequest.of(0, 1));
    if (mostRecentRoundAsList.isEmpty()) {
      return null;
    } else {
      return mostRecentRoundAsList.get(0).getUuid();
    }
  }

  private RoundScoreboard buildRoundScoreboard(final List<Round> mostRecentRoundAsList) {
    if (mostRecentRoundAsList.isEmpty()) {
      return null;
    }

    final List<SetResult> setResults =
        setResultRepository.fetchByRoundUuid(mostRecentRoundAsList.get(0).getUuid());
    final Long roundId = roundRepository.findIdByUuid(mostRecentRoundAsList.get(0).getUuid());

    Round mostRecentRound = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    if (mostRecentRound == null) {
      mostRecentRound =
          roundRepository
              .findByUuid(mostRecentRound.getUuid())
              .orElseThrow(() -> new NoSuchElementException("Round does not exist!"));
    }

    final RoundScoreboard roundScoreboard = new RoundScoreboard(mostRecentRound);
    for (final RoundGroup roundGroup : mostRecentRound.getRoundGroupsOrdered()) {
      roundScoreboard.addRoundGroupNew(roundGroup);
    }

    final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
    final String split = GeneralUtil.integerListToString(playersPerGroup);
    final String type = mostRecentRound.getSeason().getXpPointsType();
    final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplitAndType(split, type);

    roundScoreboard.assignPointsAndPlaces(xpPoints);
    return roundScoreboard;
  }

  public RoundScoreboard buildMostRecentRoundOfLeague(final UUID leagueUuid) {
    final List<Round> mostRecentRoundAsList = roundRepository.findMostRecentRoundOfLeague(leagueUuid, PageRequest.of(0, 1));
    return buildRoundScoreboard(mostRecentRoundAsList);
  }
}
