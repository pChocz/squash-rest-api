package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/scoreboards")
@RequiredArgsConstructor
public class ScoreboardController {

  private final ScoreboardService scoreboardService;
  private final SeasonService seasonService;


  @GetMapping(value = "/seasons/{seasonUuid}")
  @ResponseBody
  SeasonScoreboardDto seasonScoreboard(@PathVariable final UUID seasonUuid) {

//    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonUuid);

//    TimeLogUtil.logQuery(startTime, "Season Scoreboard: " + seasonScoreboardDto);

    return seasonScoreboardDto;
  }


  @GetMapping(value = "/most-recent-round-for-player/{playerUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForMostRecentRoundOfPlayer(@PathVariable final UUID playerUuid) {
    final RoundScoreboard roundScoreboard = scoreboardService.buildMostRecentRoundOfPlayer(playerUuid);
    return roundScoreboard;
  }


  @GetMapping(value = "/rounds/{roundUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForRound(@PathVariable final UUID roundUuid) {

//    final long startTime = System.nanoTime();
    final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundUuid);

//    TimeLogUtil.logQuery(startTime, "Round Scoreboard: " + roundScoreboard);

    return roundScoreboard;
  }

}
