package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.model.dto.scoreboard.EntireLeagueScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardRowDto;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;

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

  @Autowired
  private SeasonService seasonService;


  @GetMapping(value = "/leagues/{leagueId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'PLAYER')")
  EntireLeagueScoreboard scoreboardForLeague(
          @PathVariable final Long leagueId) {

    final long startTime = System.nanoTime();

    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueId(leagueId);
    final EntireLeagueScoreboard entireLeagueScoreboard = leagueStatsWrapper.getScoreboard();

    TimeLogUtil.logFinishWithJsonPrint(startTime, entireLeagueScoreboard);
    return entireLeagueScoreboard;
  }


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


  @GetMapping(value = "/seasons/{seasonId}")
  @ResponseBody
  SeasonScoreboardDto seasonScoreboard(
          @PathVariable("seasonId") final Long seasonId) {
    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonId);
    TimeLogUtil.logFinishWithJsonPrint(startTime, seasonScoreboardDto);
    return seasonScoreboardDto;
  }


  @GetMapping(value = "/seasons-pretenders/{seasonId}")
  @ResponseBody
  SeasonScoreboardDto seasonPretendentsScoreboard(
          @PathVariable("seasonId") final Long seasonId) {
    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonId);
    seasonScoreboardDto.getSeasonScoreboardRows().sort(Comparator.comparing(SeasonScoreboardRowDto::getCountedPointsPretenders).reversed());
    TimeLogUtil.logFinishWithJsonPrint(startTime, seasonScoreboardDto);
    return seasonScoreboardDto;
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


//  @RequestMapping(
//          value = "/perRoundScoreboard",
//          params = {"id"},
//          method = GET)
//  @ResponseBody
//  List<RoundScoreboard> perRoundScoreboard(@RequestParam("id") final Long id) {
//    final long startTime = System.nanoTime();
//    final List<RoundScoreboard> roundScoreboards = seasonService.perRoundScoreboard(id);
//    TimeLogUtil.logFinish(startTime);
//    return roundScoreboards;
//  }

}
