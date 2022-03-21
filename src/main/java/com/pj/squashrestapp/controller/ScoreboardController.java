package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/scoreboards")
@RequiredArgsConstructor
public class ScoreboardController {

  private final ScoreboardService scoreboardService;
  private final SeasonService seasonService;

  @GetMapping(value = "/all-seasons-scoreboards-for-league/{leagueUuid}")
  List<SeasonScoreboardDto> allSeasonsScoreboardsForLeague(@PathVariable final UUID leagueUuid) {
    final List<SeasonScoreboardDto> allSeasonsScoreboardsForLeague = scoreboardService.allSeasonsScoreboards(leagueUuid);
    return allSeasonsScoreboardsForLeague;
  }

  @GetMapping(value = "/all-rounds-scoreboards-for-league/{leagueUuid}")
  List<RoundScoreboard> allRoundsScoreboardsForLeague(@PathVariable final UUID leagueUuid) {
    final List<RoundScoreboard> allRoundsScoreboardsForLeague = scoreboardService.allRoundsScoreboards(leagueUuid);
    return allRoundsScoreboardsForLeague;
  }

  @GetMapping(value = "/seasons/{seasonUuid}")
  SeasonScoreboardDto getSeasonScoreboard(@PathVariable final UUID seasonUuid) {
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonUuid);
    return seasonScoreboardDto;
  }

  @GetMapping(value = "/rounds/{roundUuid}")
  RoundScoreboard getRoundScoreboard(@PathVariable final UUID roundUuid) {
    final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundUuid);
    return roundScoreboard;
  }

  @GetMapping(value = "/current-season-for-league/{leagueUuid}")
  SeasonScoreboardDto getSeasonScoreboardCurrentForLeague(@PathVariable final UUID leagueUuid) {
    final UUID currentSeasonUuid = seasonService.getCurrentSeasonUuidForLeague(leagueUuid);
    if (currentSeasonUuid == null) {
      return null;
    } else {
      return seasonService.overalScoreboard(currentSeasonUuid);
    }
  }

  @GetMapping(value = "/current-season-for-player/{playerUuid}")
  SeasonScoreboardDto getSeasonScoreboardCurrentForPlayer(@PathVariable final UUID playerUuid) {
    final UUID currentSeasonUuid = scoreboardService.getCurrentSeasonUuidForPlayer(playerUuid);
    if (currentSeasonUuid == null) {
      return null;
    } else {
      return seasonService.overalScoreboard(currentSeasonUuid);
    }
  }

  @GetMapping(value = "/most-recent-round-for-player/{playerUuid}")
  RoundScoreboard getScoreboardForMostRecentRoundOfPlayer(@PathVariable final UUID playerUuid) {
    final UUID mostRecentRoundUuid = scoreboardService.getMostRecentRoundUuidForPlayer(playerUuid);
    if (mostRecentRoundUuid == null) {
      return null;
    } else {
      return scoreboardService.buildScoreboardForRound(mostRecentRoundUuid);
    }
  }

  @GetMapping(value = "/most-recent-round-for-league/{leagueUuid}")
  RoundScoreboard getScoreboardForMostRecentRoundOfLeague(@PathVariable final UUID leagueUuid) {
    final UUID mostRecentRoundUuid = scoreboardService.getMostRecentRoundUuidForLeague(leagueUuid);
    if (mostRecentRoundUuid == null) {
      return null;
    } else {
      return scoreboardService.buildScoreboardForRound(mostRecentRoundUuid);
    }
  }

}
