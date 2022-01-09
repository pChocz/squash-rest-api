package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
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
@RequestMapping("/scoreboards")
@RequiredArgsConstructor
public class ScoreboardController {

  private final ScoreboardService scoreboardService;
  private final SeasonService seasonService;
  private final RoundService roundService;

  @GetMapping(value = "/seasons/{seasonUuid}")
  @ResponseBody
  SeasonScoreboardDto seasonScoreboard(@PathVariable final UUID seasonUuid) {
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonUuid);
    return seasonScoreboardDto;
  }

  @GetMapping(value = "/rounds/{roundUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForRound(@PathVariable final UUID roundUuid) {
    final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundUuid);
    return roundScoreboard;
  }

  @GetMapping(value = "/current-season-for-league/{leagueUuid}")
  @ResponseBody
  SeasonScoreboardDto seasonScoreboardCurrentForLeague(@PathVariable final UUID leagueUuid) {
    final UUID currentSeasonUuid = seasonService.getCurrentSeasonUuidForLeague(leagueUuid);
    if (currentSeasonUuid == null) {
      return null;
    } else {
      return seasonService.overalScoreboard(currentSeasonUuid);
    }
  }

  @GetMapping(value = "/most-recent-round-for-player/{playerUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForMostRecentRoundOfPlayer(@PathVariable final UUID playerUuid) {
    final UUID mostRecentRoundUuid = scoreboardService.getMostRecentRoundUuidForPlayer(playerUuid);
    if (mostRecentRoundUuid == null) {
      return null;
    } else {
      return scoreboardService.buildScoreboardForRound(mostRecentRoundUuid);
    }
  }

  @GetMapping(value = "/most-recent-round-for-league/{leagueUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForMostRecentRoundOfLeague(@PathVariable final UUID leagueUuid) {
    final UUID mostRecentRoundUuid = scoreboardService.getMostRecentRoundUuidForLeague(leagueUuid);
    if (mostRecentRoundUuid == null) {
      return null;
    } else {
      return scoreboardService.buildScoreboardForRound(mostRecentRoundUuid);
    }
  }

}
