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


  @GetMapping(value = "/leagues/{leagueId}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'PLAYER')")
  EntireLeagueScoreboard scoreboardForLeague(
          @PathVariable final Long leagueId) {

    final long startTime = System.nanoTime();
    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueId(leagueId);
    final EntireLeagueScoreboard entireLeagueScoreboard = leagueStatsWrapper.getScoreboard();
    TimeLogUtil.logFinish(startTime);

    return entireLeagueScoreboard;
  }


  @GetMapping(value = "/leagues/{leagueId}/players/{playersIds}")
  @ResponseBody
  Scoreboard scoreboardForLeagueForOneOrSeveralPlayers(
          @PathVariable final Long leagueId,
          @PathVariable final Long[] playersIds) {

    final long startTime = System.nanoTime();
    final Scoreboard scoreboard = (playersIds.length == 1)
            ? scoreboardService.buildScoreboardForLeagueForSinglePlayer(leagueId, playersIds[0])
            : scoreboardService.buildScoreboardForLeagueForPlayers(leagueId, playersIds);

    final String playersCommaSeparated = scoreboard
            .getScoreboardRows()
            .stream()
            .map(ScoreboardRow::getPlayer)
            .map(PlayerDto::getUsername)
            .collect(Collectors.joining(", ", "[", "]"));

    TimeLogUtil.logQuery(startTime, "Players stats: " + playersCommaSeparated);

    return scoreboard;
  }


  /**
   * TODO: ONLY FOR TESTING PURPOSES!!
   */
  @GetMapping(value = "/matches/leagues/{leagueId}/players/{playersIds}")
  @ResponseBody
  List<MatchDto> matchOnlyForLeagueForOneOrSeveralPlayers(
          @PathVariable final Long leagueId,
          @PathVariable final Long[] playersIds) {

    final long startTime = System.nanoTime();
    final Scoreboard scoreboard = (playersIds.length == 1)
            ? scoreboardService.buildScoreboardForLeagueForSinglePlayer(leagueId, playersIds[0])
            : scoreboardService.buildScoreboardForLeagueForPlayers(leagueId, playersIds);
    TimeLogUtil.logFinish(startTime);

    return scoreboard.getMatches();
  }


  @GetMapping(value = "/seasons/{seasonId}/players-sorted")
  @ResponseBody
  List<PlayerDto> leaguePlayersSeasonSorted(
          @PathVariable("seasonId") final Long seasonId) {

    final long startTime = System.nanoTime();
    final List<PlayerDto> players = seasonService.extractLeaguePlayersSortedByPointsInSeason(seasonId);
    TimeLogUtil.logFinish(startTime);

    return players;
  }

  @GetMapping(value = "/seasons/{seasonId}")
  @ResponseBody
  SeasonScoreboardDto seasonScoreboard(
          @PathVariable("seasonId") final Long seasonId) {

    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonId);

    final String seasonScoreboardDescription = "S: " + seasonScoreboardDto.getSeason().getSeasonNumber()
                                              + "\t| " + seasonScoreboardDto.getSeason().getLeagueName();

    TimeLogUtil.logQuery(startTime, "Season Scoreboard: " + seasonScoreboardDescription);

    return seasonScoreboardDto;
  }


  @GetMapping(value = "/seasons-pretenders/{seasonId}")
  @ResponseBody
  SeasonScoreboardDto seasonPretendentsScoreboard(
          @PathVariable("seasonId") final Long seasonId) {

    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(seasonId);
    seasonScoreboardDto.sortByPretendersPoints();
    TimeLogUtil.logFinish(startTime);

    return seasonScoreboardDto;
  }


  @GetMapping(value = "/rounds/{roundId}")
  @ResponseBody
  RoundScoreboard scoreboardForRound(
          @PathVariable final Long roundId) {

    final long startTime = System.nanoTime();
    final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundId);

    final String roundScoreboardDescription = "R: "
                                              + roundScoreboard.getRoundNumber()
                                              + "\t| S: " + roundScoreboard.getSeasonNumber()
                                              + "\t| " + roundScoreboard.getLeagueName();

    TimeLogUtil.logQuery(startTime, "Round Scoreboard: " + roundScoreboardDescription);

    return roundScoreboard;
  }

}
