package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.model.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.model.dto.scoreboard.ScoreboardRow;
import com.pj.squashrestapp.model.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequiredArgsConstructor
public class ScoreboardController {

  private final ScoreboardService scoreboardService;
  private final LeagueService leagueService;
  private final SeasonService seasonService;


  @GetMapping(value = "/leagues/{leagueUuid}/players/{playersUuids}")
  @ResponseBody
  Scoreboard scoreboardForLeagueForOneOrSeveralPlayers(
          @PathVariable final UUID leagueUuid,
          @PathVariable final UUID[] playersUuids) {

    final long startTime = System.nanoTime();
    final Scoreboard scoreboard = (playersUuids.length == 1)
//            ? scoreboardService.buildScoreboardForLeagueForSinglePlayer(leagueUuid, playersIds[0])
//            : scoreboardService.buildScoreboardForLeagueForPlayers(leagueUuid, playersIds);
            ? scoreboardService.buildScoreboardForLeagueForSinglePlayerNEW(leagueUuid, playersUuids[0])
            : scoreboardService.buildScoreboardForLeagueForPlayersNEW(leagueUuid, playersUuids);

    // workaround! applying pagination
    scoreboard.removeMatches();

    final String playersCommaSeparated = scoreboard
            .getScoreboardRows()
            .stream()
            .map(ScoreboardRow::getPlayer)
            .map(PlayerDto::getUsername)
            .collect(Collectors.joining(", ", "[", "]"));

    TimeLogUtil.logQuery(startTime, "Players stats: " + playersCommaSeparated);

    return scoreboard;
  }


  @GetMapping(value = "/seasons/{seasonUuid}/players-sorted")
  @ResponseBody
  List<PlayerDto> leaguePlayersSeasonSorted(
          @PathVariable final UUID seasonUuid) {

    final long startTime = System.nanoTime();
    final List<PlayerDto> players = seasonService.extractLeaguePlayersSortedByPointsInSeason(seasonUuid);
    TimeLogUtil.logFinish(startTime);

    return players;
  }


  @GetMapping(value = "/seasons/{seasonUuid}")
  @ResponseBody
  SeasonScoreboardDto seasonScoreboard(
          @PathVariable final UUID seasonUuid) {

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
          @PathVariable final UUID seasonUuid) {

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
