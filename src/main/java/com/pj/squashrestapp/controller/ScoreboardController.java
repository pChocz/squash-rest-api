package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.Scoreboard;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/scoreboards")
public class ScoreboardController {

  @Autowired
  private SetResultRepository setResultRepository;

  @RequestMapping(
          value = "/bySeveralPlayers",
          params = {"leagueId", "playerIds"},
          method = GET)
  @ResponseBody
  Scoreboard bySeveralPlayers(
          @RequestParam("playersIds") final Long[] playersIds,
          @RequestParam("leagueId") final Long leagueId) {
    final long startTime = System.nanoTime();

    final List<SetResult> setResults = setResultRepository.fetchBySeveralPlayersIdsAndLeagueId(leagueId, playersIds);
    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, leagueId);
    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);
    final Scoreboard scoreboard = new Scoreboard(matches);

    TimeLogUtil.logFinishWithJsonPrint(startTime, scoreboard);
    return scoreboard;
  }

//  @RequestMapping(
//          value = "/byRoundGroupId",
//          params = {"id"},
//          method = GET)
//  @ResponseBody
//  Scoreboard byRoundId(
//          @RequestParam("id") final Long id) {
//    final List<SingleSetRowDto> sets = matchRepository.retrieveByRoundGroupId(id);
//    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
//    final Scoreboard scoreboard = new Scoreboard(matches);
//    return scoreboard;
//  }

  @RequestMapping(
          value = "/byLeagueId",
          params = {"id"},
          method = GET)
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#id, 'PLAYER')")
  Scoreboard byLeagueId(
          @RequestParam("id") final Long id) {
    final List<SetResult> setResults = setResultRepository.fetchByLeagueId(id);
    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, id);
    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);
    final Scoreboard scoreboard = new Scoreboard(matches);
    return scoreboard;
  }

}
