package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.QueryLog;
import com.pj.squashrestapp.dto.scoreboard.PlayerSummary;
import com.pj.squashrestapp.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.service.PlayersScoreboardService;
import com.pj.squashrestapp.util.GeneralUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/players-scoreboards")
@RequiredArgsConstructor
public class PlayersScoreboardController {

  private final PlayersScoreboardService playersScoreboardService;

  @GetMapping(value = "/{leagueUuid}/{playersUuids}")
  @ResponseBody
  @QueryLog
  Scoreboard extractAllAgainstAll(
      @PathVariable final UUID leagueUuid,
      @PathVariable final UUID[] playersUuids,
      @RequestParam(required = false) final UUID seasonUuid,
      @RequestParam(required = false) final Integer groupNumber,
      @RequestParam final boolean includeAdditionalMatches) {

    final Scoreboard scoreboard =
        (playersUuids.length == 1)
            ? playersScoreboardService.buildSingle(
                leagueUuid, playersUuids[0], seasonUuid, groupNumber, includeAdditionalMatches)
            : playersScoreboardService.buildMultipleAllAgainstAll(
                leagueUuid, playersUuids, seasonUuid, groupNumber, includeAdditionalMatches);

    return scoreboard;
  }

  @GetMapping(value = "/me-against-all/{leagueUuid}")
  @ResponseBody
  Scoreboard extractMeAgainstAllForLeague(@PathVariable final UUID leagueUuid) {
    final Scoreboard scoreboard = playersScoreboardService.buildMultipleMeAgainstAll(leagueUuid);
    return scoreboard;
  }

  @GetMapping(value = "/me-against-all")
  @ResponseBody
  @QueryLog
  PlayerSummary extractMeAgainstAllForAllLeagues() {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final PlayerSummary playerSummary = playersScoreboardService.buildPlayerAgainstAllForAllLeagues(currentPlayerUuid);
    return playerSummary;
  }
}
