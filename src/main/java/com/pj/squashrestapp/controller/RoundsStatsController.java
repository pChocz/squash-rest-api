package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.playerroundsstats.PlayerAllRoundsStats;
import com.pj.squashrestapp.service.PlayersRoundsStatsService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/rounds-stats")
@RequiredArgsConstructor
public class RoundsStatsController {

  private final PlayersRoundsStatsService playersRoundsStatsService;

  @GetMapping(value = "/{leagueUuid}/{playerUuid}")
  @ResponseBody
  PlayerAllRoundsStats extractRoundsStats(
      @PathVariable final UUID leagueUuid, @PathVariable final UUID playerUuid) {
    final PlayerAllRoundsStats roundsStatsForPlayer =
        playersRoundsStatsService.buildRoundsStatsForPlayer(leagueUuid, playerUuid);
    return roundsStatsForPlayer;
  }
}
