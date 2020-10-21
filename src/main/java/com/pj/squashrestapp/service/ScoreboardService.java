package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreboardService {

  private final LeagueRepository leagueRepository;
  private final RoundRepository roundRepository;
  private final MatchRepository matchRepository;
  private final SetResultRepository setResultRepository;
  private final XpPointsRepository xpPointsRepository;


  public RoundScoreboard buildScoreboardForRound(final UUID roundUuid) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundUuid(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);

    Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    if (round == null) {
      round = roundRepository
              .findByUuid(roundUuid)
              .orElseThrow(() -> new NoSuchElementException("Round does not exist!"));
    }

    final int currentRoundNumber = round.getNumber();
    final Season currentSeason = round.getSeason();

    final UUID previousRoundUuid = roundRepository
            .findBySeasonAndNumber(currentSeason, currentRoundNumber - 1)
            .map(Round::getUuid)
            .orElse(null);

    final UUID nextRoundUuid = roundRepository
            .findBySeasonAndNumber(currentSeason, currentRoundNumber + 1)
            .map(Round::getUuid)
            .orElse(null);

    final RoundScoreboard roundScoreboard = new RoundScoreboard(round, previousRoundUuid, nextRoundUuid);
    for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
      roundScoreboard.addRoundGroupNew(roundGroup);
    }

    final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
    final String split = GeneralUtil.integerListToString(playersPerGroup);
    final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplit(split);

    roundScoreboard.assignPointsAndPlaces(xpPoints);
    return roundScoreboard;
  }


//  public Scoreboard buildScoreboardForLeagueForPlayers(final UUID leagueUuid, final Long[] playersIds) {
//    final List<SetResult> setResults = setResultRepository.fetchBySeveralPlayersIdsAndLeagueId(leagueUuid, playersIds);
//
//    if (setResults.isEmpty()) {
//      return new Scoreboard(new ArrayList<>());
//    }
//
//    final Long leagueId = leagueRepository.findIdByUuid(leagueUuid);
//    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, leagueId);
//
//    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);
//    final Scoreboard scoreboard = new Scoreboard(matches);
//
//    return scoreboard;
//  }
//
//
//  public Scoreboard buildScoreboardForLeagueForSinglePlayer(final UUID leagueUuid, final Long playerId) {
//    final List<SetResult> setResults = setResultRepository.fetchByOnePlayerIdAndLeagueId(leagueUuid, playerId);
//    final Long leagueId = leagueRepository.findIdByUuid(leagueUuid);
//    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, leagueId);
//
//    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);
//    final Scoreboard scoreboard = new Scoreboard(matches);
//    scoreboard.makeItSinglePlayerScoreboard(playerId);
//
//    return scoreboard;
//  }

}
