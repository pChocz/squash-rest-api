package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.PlayerLeagueXpOveral;
import com.pj.squashrestapp.model.dto.RoundScoreboard;
import com.pj.squashrestapp.model.dto.SeasonScoreboardDto;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/league")
@CrossOrigin(origins = "http://localhost:4200")
public class LeagueController {

  @Autowired
  private LeagueService leagueService;

  @RequestMapping(
          value = "/overalScoreboard",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<SeasonScoreboardDto> overalScoreboard(@RequestParam("id") final Long id) {
    final long startTime = System.nanoTime();

    final List<SeasonScoreboardDto> seasonScoreboardDtoList = leagueService.overalScoreboard(id);

    TimeLogUtil.logFinish(startTime);
    return seasonScoreboardDtoList;
  }

  @RequestMapping(
          value = "/overalXpPoints",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<PlayerLeagueXpOveral> overalXpPoints(@RequestParam("id") final Long id) {
    final long startTime = System.nanoTime();

    final List<PlayerLeagueXpOveral> playerLeagueXpOveralList = leagueService.overalXpPoints(id);

    TimeLogUtil.logFinish(startTime);
    return playerLeagueXpOveralList;
  }

}
