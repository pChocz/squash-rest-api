package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.LeagueStatsWrapper;
import com.pj.squashrestapp.service.LeagueStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
  private LeagueStatisticsService leagueStatisticsService;

  @RequestMapping(
          value = "/byId",
          params = {"id"},
          method = GET)
  @ResponseBody
  LeagueStatsWrapper byLeagueId(@RequestParam("id") final Long id) {
    return leagueStatisticsService.buildStatsForLeagueId(id);
  }

}
