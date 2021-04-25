package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.QueryLog;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
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
  @QueryLog
  SeasonScoreboardDto seasonScoreboard(@PathVariable final UUID seasonUuid) {
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonUuid);
    return seasonScoreboardDto;
  }

  @GetMapping(value = "/current-season-for-league/{leagueUuid}")
  @ResponseBody
  SeasonScoreboardDto scoreboardForCurrentSeasonOfLeague(@PathVariable final UUID leagueUuid) {
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.buildCurrentSeasonScoreboardOfLeague(leagueUuid);
    return seasonScoreboardDto;
  }


  @GetMapping(value = "/most-recent-round-for-player/{playerUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForMostRecentRoundOfPlayer(@PathVariable final UUID playerUuid) {
    final RoundScoreboard roundScoreboard = scoreboardService.buildMostRecentRoundOfPlayer(playerUuid);
    return roundScoreboard;
  }


  @GetMapping(value = "/most-recent-round-for-league/{leagueUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForMostRecentRoundOfLeague(@PathVariable final UUID leagueUuid) {
    final RoundScoreboard roundScoreboard = scoreboardService.buildMostRecentRoundOfLeague(leagueUuid);
    return roundScoreboard;
  }


  @GetMapping(value = "/rounds/{roundUuid}")
  @ResponseBody
  @QueryLog
  RoundScoreboard scoreboardForRound(@PathVariable final UUID roundUuid) {
    final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundUuid);
    return roundScoreboard;
  }

}
