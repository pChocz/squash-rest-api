package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.playerroundsstats.PlayerSingleRoundStats;
import com.pj.squashrestapp.model.dto.scoreboard.PlayerSummary;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.ScoreboardRow;
import com.pj.squashrestapp.service.PlayersRoundsStatsService;
import com.pj.squashrestapp.service.PlayersScoreboardService;
import com.pj.squashrestapp.util.LogUtil;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/players-scoreboards")
@RequiredArgsConstructor
public class PlayersScoreboardController {

  private final PlayersScoreboardService playersScoreboardService;
  private final PlayersRoundsStatsService playersRoundsStatsService;

  @GetMapping(value = "/rounds-stats")
  @ResponseBody
  List<PlayerSingleRoundStats> extractRoundsStats(@RequestParam final UUID leagueUuid,
                                                  @RequestParam final UUID playerUuid) {
    final long startTime = System.nanoTime();

    final List<PlayerSingleRoundStats> ret = playersRoundsStatsService.buildRoundsStatsForPlayer(leagueUuid, playerUuid);

    TimeLogUtil.logQuery(startTime, "Player rounds stats: " + playerUuid);
    return ret;
  }

  @GetMapping(value = "/leagues/{leagueUuid}/players/{playersUuids}")
  @ResponseBody
  Scoreboard extractAllAgainstAll(@PathVariable final UUID leagueUuid,
                                  @PathVariable final UUID[] playersUuids,
                                  @RequestParam(required = false) final UUID seasonUuid,
                                  @RequestParam(required = false) final Integer groupNumber) {

    final long startTime = System.nanoTime();

    final Scoreboard scoreboard = (playersUuids.length == 1)
            ? playersScoreboardService.buildSingle(leagueUuid, playersUuids[0], seasonUuid, groupNumber)
            : playersScoreboardService.buildMultipleAllAgainstAll(leagueUuid, playersUuids, seasonUuid, groupNumber);

    TimeLogUtil.logQuery(startTime, "ALL-AGAINST-ALL stats: " + LogUtil.extractPlayersCommaSeparated(scoreboard));
    return scoreboard;
  }

  @GetMapping(value = "/leagues/{leagueUuid}/players/{playersUuids}/me-against-all")
  @ResponseBody
  Scoreboard extractMeAgainstAll(@PathVariable final UUID leagueUuid,
                                 @PathVariable final UUID[] playersUuids) {

    final long startTime = System.nanoTime();

    final Scoreboard scoreboard = playersScoreboardService.buildMultipleMeAgainstAll(leagueUuid, playersUuids);

    TimeLogUtil.logQuery(startTime, "ME-AGAINST-ALL stats: " + LogUtil.extractPlayersCommaSeparated(scoreboard));
    return scoreboard;
  }

  @GetMapping(value = "/me-against-all")
  @ResponseBody
  PlayerSummary extractMeAgainstAllForAllLeagues() {
    final PlayerSummary playerSummary = playersScoreboardService.buildMeAgainstAllForAllLeagues();
    return playerSummary;
  }

}
