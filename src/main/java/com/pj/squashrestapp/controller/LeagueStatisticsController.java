package com.pj.squashrestapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.SimpleMatchDto;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import com.pj.squashrestapp.service.LeagueStatsWrapper;
import com.pj.squashrestapp.service.LeagueStatisticsService;
import com.pj.squashrestapp.util.MatchUtil;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/league-stats")
public class LeagueStatisticsController {

  @Autowired
  private LeagueStatisticsService leagueStatisticsService;

  @RequestMapping(
          value = "/byId",
          params = {"id"},
          method = GET)
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#id, 'PLAYER')")
  LeagueStatsWrapper byId(@RequestParam("id") final Long id) {
    final long startTime = System.nanoTime();

    final LeagueStatsWrapper leagueStatsWrapper = leagueStatisticsService.buildStatsForLeagueId(id);

    TimeLogUtil.logFinishWithJsonPrint(startTime, leagueStatsWrapper);
    return leagueStatsWrapper;
  }

  @RequestMapping(
          value = "/matches2",
          params = {"leagueId"},
          method = GET)
  @ResponseBody
  List<MatchDto> matches2(@RequestParam("leagueId") final Long leagueId) {
    final long startTime = System.nanoTime();

    final List<SingleSetRowDto> sets = leagueStatisticsService.getMatchRepository().retrieveByLeagueId(leagueId);
    final List<MatchDto> matchDtos = MatchUtil.rebuildMatches(sets);
    final List<MatchDto> matches = new ArrayList<>();

    int counter = 0;
    for (final MatchDto match : matchDtos) {
      counter++;
      log.info(match.toString());
      matches.add(match);
    }

    TimeLogUtil.logFinish(startTime, counter);
    return matches;
  }

  @RequestMapping(
          value = "/matches1",
          params = {"leagueId"},
          method = GET)
  @ResponseBody
  List<SimpleMatchDto> matches1(@RequestParam("leagueId") final Long leagueId) {
    final long startTime = System.nanoTime();

    final League league = leagueStatisticsService.getLeagueRepository().findWithEntityGraphById(leagueId);
    final List<SimpleMatchDto> matches = new ArrayList<>();

    int counter = 0;
    for (final Season season : league.getSeasons()) {
      for (final Round round : season.getRounds()) {
        for (final RoundGroup roundGroup : round.getRoundGroups()) {
          for (final Match match : roundGroup.getMatches()) {
            counter++;
            final SimpleMatchDto simpleMatchDto = new SimpleMatchDto(match);
            log.info(simpleMatchDto.toString());
            matches.add(simpleMatchDto);
          }
        }
      }
    }

    TimeLogUtil.logFinish(startTime, counter);
    return matches;
  }

}
