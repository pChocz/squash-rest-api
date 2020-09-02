package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.model.dto.scoreboard.EntireLeagueScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.ScoreboardRow;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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


  // TODO: can be removed I guess!
  @GetMapping(value = "/leagues/{leagueUuid}")
  @ResponseBody
//  @PreAuthorize("hasRoleForLeague(#leagueId, 'PLAYER')")
  EntireLeagueScoreboard scoreboardForLeague(
          @PathVariable final UUID leagueUuid) {

    final long startTime = System.nanoTime();
    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueId(leagueUuid);
    final EntireLeagueScoreboard entireLeagueScoreboard = leagueStatsWrapper.getScoreboard();
    TimeLogUtil.logFinish(startTime);

    return entireLeagueScoreboard;
  }


  @GetMapping(value = "/leagues/{leagueUuid}/players/{playersIds}")
  @ResponseBody
  Scoreboard scoreboardForLeagueForOneOrSeveralPlayers(
          @PathVariable final UUID leagueUuid,
          @PathVariable final Long[] playersIds) {

    final long startTime = System.nanoTime();
    final Scoreboard scoreboard = (playersIds.length == 1)
            ? scoreboardService.buildScoreboardForLeagueForSinglePlayer(leagueUuid, playersIds[0])
            : scoreboardService.buildScoreboardForLeagueForPlayers(leagueUuid, playersIds);

    final String playersCommaSeparated = scoreboard
            .getScoreboardRows()
            .stream()
            .map(ScoreboardRow::getPlayer)
            .map(PlayerDto::getUsername)
            .collect(Collectors.joining(", ", "[", "]"));

    TimeLogUtil.logQuery(startTime, "Players stats: " + playersCommaSeparated);

    return scoreboard;
  }


//  /**
//   * TODO: ONLY FOR TESTING PURPOSES!!
//   */
//  @GetMapping(value = "/matches/leagues/{leagueUuid}/players/{playersIds}")
//  @ResponseBody
//  List<MatchDto> matchOnlyForLeagueForOneOrSeveralPlayers(
//          @PathVariable final UUID leagueUuid,
//          @PathVariable final Long[] playersIds) {
//
//    final long startTime = System.nanoTime();
//    final Scoreboard scoreboard = (playersIds.length == 1)
//            ? scoreboardService.buildScoreboardForLeagueForSinglePlayer(leagueUuid, playersIds[0])
//            : scoreboardService.buildScoreboardForLeagueForPlayers(leagueUuid, playersIds);
//    TimeLogUtil.logFinish(startTime);
//
//    return scoreboard.getMatches();
//  }


  @GetMapping(value = "/seasons/{seasonUuid}/players-sorted")
  @ResponseBody
  List<PlayerDto> leaguePlayersSeasonSorted(
          @PathVariable("seasonUuid") final UUID seasonUuid) {

    final long startTime = System.nanoTime();
    final List<PlayerDto> players = seasonService.extractLeaguePlayersSortedByPointsInSeason(seasonUuid);
    TimeLogUtil.logFinish(startTime);

    return players;
  }

  @GetMapping(value = "/seasons/{seasonUuid}")
  @ResponseBody
  SeasonScoreboardDto seasonScoreboard(
          @PathVariable("seasonUuid") final UUID seasonUuid) {

    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonUuid);

    final String seasonScoreboardDescription = "S: " + seasonScoreboardDto.getSeason().getSeasonNumber()
                                              + "\t| " + seasonScoreboardDto.getSeason().getLeagueName();

    TimeLogUtil.logQuery(startTime, "Season Scoreboard: " + seasonScoreboardDescription);

    return seasonScoreboardDto;
  }


  @GetMapping(value = "/seasons-pretenders/{seasonUuid}")
  @ResponseBody
  SeasonScoreboardDto seasonPretendentsScoreboard(
          @PathVariable("seasonUuid") final UUID seasonUuid) {

    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonUuid);
    seasonScoreboardDto.sortByPretendersPoints();
    TimeLogUtil.logFinish(startTime);

    return seasonScoreboardDto;
  }


  @GetMapping(value = "/rounds/{roundUuid}")
  @ResponseBody
  RoundScoreboard scoreboardForRound(
          @PathVariable final UUID roundUuid) {

    final long startTime = System.nanoTime();
    final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundUuid);

    final String roundScoreboardDescription = "R: "
                                              + roundScoreboard.getRoundNumber()
                                              + "\t| S: " + roundScoreboard.getSeasonNumber()
                                              + "\t| " + roundScoreboard.getLeagueName();

    TimeLogUtil.logQuery(startTime, "Round Scoreboard: " + roundScoreboardDescription);

    return roundScoreboard;
  }

}
