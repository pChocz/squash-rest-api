package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.LeagueStatsWrapper;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/league-stats")
public class LeagueStatisticsController {

  @Autowired
  private LeagueService leagueService;

  @RequestMapping(
          value = "/byId",
          params = {"leagueId"},
          method = GET)
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'PLAYER')")
  LeagueStatsWrapper byId(@RequestParam("leagueId") final Long leagueId) {
    final long startTime = System.nanoTime();

    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueId(leagueId);

    TimeLogUtil.logFinishWithJsonPrint(startTime, leagueStatsWrapper);
    return leagueStatsWrapper;
  }

}
