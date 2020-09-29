package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.dto.match.MatchDto;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
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
import java.util.UUID;
import java.util.stream.Collectors;

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
    final List<SetResult> setResults = setResultRepository.fetchByRoundId(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);
    final Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);

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


  public Scoreboard buildScoreboardForLeagueForPlayersNEW(final UUID leagueUuid, final UUID[] playersUuids) {
    final List<Match> matches = matchRepository.fetchBySeveralPlayersIdsAndLeagueId(leagueUuid, playersUuids);

    final List<MatchDto> matchesDtos = matches
            .stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);

    return scoreboard;
  }

  public Scoreboard buildScoreboardForLeagueForSinglePlayerNEW(final UUID leagueUuid, final UUID playerUuid) {
    final List<Match> matches = matchRepository.fetchByOnePlayerIdAndLeagueId(leagueUuid, playerUuid);

    final List<MatchDto> matchesDtos = matches
            .stream()
            .map(MatchDetailedDto::new)
            .collect(Collectors.toList());

    final Scoreboard scoreboard = new Scoreboard(matchesDtos);
    scoreboard.makeItSinglePlayerScoreboard(playerUuid);

    return scoreboard;
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
