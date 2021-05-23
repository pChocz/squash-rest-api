package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.ErrorCode;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  public RoundScoreboard buildScoreboardForRound(final UUID roundUuid) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);

    Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    if (round == null) {
      round =
          roundRepository
              .findByUuid(roundUuid)
              .orElseThrow(() -> new NoSuchElementException(ErrorCode.ROUND_NOT_FOUND));
    }

    final int currentRoundNumber = round.getNumber();
    final Season currentSeason = round.getSeason();

    final UUID previousRoundUuid =
        roundRepository
            .findBySeasonAndNumber(currentSeason, currentRoundNumber - 1)
            .map(Round::getUuid)
            .orElse(null);

    final UUID nextRoundUuid =
        roundRepository
            .findBySeasonAndNumber(currentSeason, currentRoundNumber + 1)
            .map(Round::getUuid)
            .orElse(null);

    final RoundScoreboard roundScoreboard =
        new RoundScoreboard(round, previousRoundUuid, nextRoundUuid);
    for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
      roundScoreboard.addRoundGroupNew(roundGroup);
    }

    final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
    final String split = GeneralUtil.integerListToString(playersPerGroup);
    final String type = currentSeason.getXpPointsType();
    final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplitAndType(split, type);

    roundScoreboard.assignPointsAndPlaces(xpPoints);
    return roundScoreboard;
  }

  public RoundScoreboard buildMostRecentRoundOfPlayer(final UUID playerUuid) {
    final List<Round> mostRecentRoundAsList =
        roundRepository.findMostRecentRoundOfPlayer(playerUuid, PageRequest.of(0, 1));
    return buildRoundScoreboard(mostRecentRoundAsList);
  }

  private RoundScoreboard buildRoundScoreboard(final List<Round> mostRecentRoundsAsList) {
    if (mostRecentRoundsAsList.isEmpty()) {
      return null;
    }

    final UUID roundUuid = mostRecentRoundsAsList.get(0).getUuid();
    final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);

    Round mostRecentRound = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    if (mostRecentRound == null) {
      mostRecentRound =
          roundRepository
              .findByUuid(mostRecentRound.getUuid())
              .orElseThrow(() -> new NoSuchElementException(ErrorCode.ROUND_NOT_FOUND));
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
    final List<Round> mostRecentRoundAsList =
        roundRepository.findMostRecentRoundOfLeague(leagueUuid, PageRequest.of(0, 1));
    return buildRoundScoreboard(mostRecentRoundAsList);
  }
}
