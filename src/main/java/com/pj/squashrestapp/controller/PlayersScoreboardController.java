package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.service.PlayersScoreboardService;
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
@RequestMapping("/players-scoreboards")
@RequiredArgsConstructor
public class PlayersScoreboardController {

  private final PlayersScoreboardService playersScoreboardService;

  @GetMapping(value = "/leagues/{leagueUuid}/players/{playersUuids}")
  @ResponseBody
  Scoreboard extractAllAgainstAll(@PathVariable final UUID leagueUuid,
                                  @PathVariable final UUID[] playersUuids) {

    final long startTime = System.nanoTime();

    final Scoreboard scoreboard = (playersUuids.length == 1)
            ? playersScoreboardService.buildSingle(leagueUuid, playersUuids[0])
            : playersScoreboardService.buildMultipleAllAgainstAll(leagueUuid, playersUuids);

    TimeLogUtil.logQuery(startTime, "Players stats: " + LogUtil.extractPlayersCommaSeparated(scoreboard));
    return scoreboard;
  }

  @GetMapping(value = "/leagues/{leagueUuid}/players/{playersUuids}/me-against-all")
  @ResponseBody
  Scoreboard extractMeAgainstAll(@PathVariable final UUID leagueUuid,
                                 @PathVariable final UUID[] playersUuids) {

    final long startTime = System.nanoTime();

    final Scoreboard scoreboard = playersScoreboardService.buildMultipleMeAgainstAll(leagueUuid, playersUuids);

    TimeLogUtil.logQuery(startTime, "Players stats: " + LogUtil.extractPlayersCommaSeparated(scoreboard));
    return scoreboard;
  }

}
