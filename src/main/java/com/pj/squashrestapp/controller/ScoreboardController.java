package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.model.dto.scoreboard.EntireLeagueScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/scoreboards")
public class ScoreboardController {

  @Autowired
  private ScoreboardService scoreboardService;

  @Autowired
  private LeagueService leagueService;

  @GetMapping(value = "/leagues/{leagueId}/players/{playersIds}")
  @ResponseBody
  Scoreboard scoreboardForLeagueForSeveralPlayers(
          @PathVariable final Long leagueId,
          @PathVariable final Long[] playersIds) {

    final long startTime = System.nanoTime();
    final Scoreboard scoreboard = scoreboardService.buildScoreboardForLeagueForPlayers(leagueId, playersIds);

    TimeLogUtil.logFinishWithJsonPrint(startTime, scoreboard);
    return scoreboard;
  }

  @GetMapping(value = "/rounds/{roundId}")
  @ResponseBody
  RoundScoreboard scoreboardForRound(
          @PathVariable final Long roundId) {

    final long startTime = System.nanoTime();
    final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundId);

    TimeLogUtil.logFinishWithJsonPrint(startTime, roundScoreboard);
    return roundScoreboard;
  }

  @GetMapping(value = "/leagues/{leagueId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#id, 'PLAYER')")
  EntireLeagueScoreboard scoreboardForLeague(
          @PathVariable final Long leagueId) {

    final long startTime = System.nanoTime();
    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueId(leagueId);
    final EntireLeagueScoreboard entireLeagueScoreboard = leagueStatsWrapper.getScoreboard();

    TimeLogUtil.logFinishWithJsonPrint(startTime, entireLeagueScoreboard);
    return entireLeagueScoreboard;
  }

}
