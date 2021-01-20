package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.playerroundsstats.PlayerSingleRoundStats;
import com.pj.squashrestapp.service.PlayersRoundsStatsService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/rounds-stats")
@RequiredArgsConstructor
public class RoundsStatsController {

  private final PlayersRoundsStatsService playersRoundsStatsService;

  @GetMapping(value = "/{leagueUuid}/{playerUuid}")
  @ResponseBody
  List<PlayerSingleRoundStats> extractRoundsStats(@PathVariable final UUID leagueUuid,
                                                  @PathVariable final UUID playerUuid) {
    final long startTime = System.nanoTime();

    final List<PlayerSingleRoundStats> roundsStatsForPlayer = playersRoundsStatsService.buildRoundsStatsForPlayer(leagueUuid, playerUuid);

    TimeLogUtil.logQuery(startTime, "Player rounds stats: " + playerUuid);
    return roundsStatsForPlayer;
  }

}
