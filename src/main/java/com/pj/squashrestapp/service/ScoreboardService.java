package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 *
 */
@Service
public class ScoreboardService {

  @Autowired
  private SetResultRepository setResultRepository;

  @Autowired
  private XpPointsRepository xpPointsRepository;

  public RoundScoreboard buildScoreboardForRound(final Long roundId) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundId(roundId);
    final Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);

    final RoundScoreboard roundScoreboard = new RoundScoreboard(round);
    for (final RoundGroup roundGroup : round.getRoundGroups()) {
      roundScoreboard.addRoundGroupNew(roundGroup);
    }

    final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
    final String split = GeneralUtil.integerListToString(playersPerGroup);
    final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplit(split);

    roundScoreboard.assignPointsAndPlaces(xpPoints);
    return roundScoreboard;
  }

  public Scoreboard buildScoreboardForLeague(final Long leagueId) {
    final List<SetResult> setResults = setResultRepository.fetchByLeagueId(leagueId);
    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, leagueId);

    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);
    final Scoreboard leagueScoreboard = new Scoreboard(matches);

    return leagueScoreboard;
  }

  public Scoreboard buildScoreboardForLeagueForPlayers(final Long leagueId, final Long[] playersIds) {
    final List<SetResult> setResults = setResultRepository.fetchBySeveralPlayersIdsAndLeagueId(leagueId, playersIds);
    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, leagueId);

    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);
    final Scoreboard scoreboard = new Scoreboard(matches);

    return scoreboard;
  }

}
