package com.pj.squashrestapp.controller;

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
@RequestMapping("/season")
@CrossOrigin(origins = "http://localhost:4200")
public class SeasonController {

  @Autowired
  private SeasonService seasonService;

  @RequestMapping(
          value = "/overalScoreboard",
          params = {"id"},
          method = GET)
  @ResponseBody
  public SeasonScoreboardDto overalScoreboard(@RequestParam("id") final Long id) {
    final long startTime = System.nanoTime();
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(id);
    TimeLogUtil.logFinish(startTime);
    return seasonScoreboardDto;
  }

//  @RequestMapping(
//          value = "/perRoundScoreboard",
//          params = {"id"},
//          method = GET)
//  @ResponseBody
//  List<RoundScoreboard> perRoundScoreboard(@RequestParam("id") final Long id) {
//    final long startTime = System.nanoTime();
//    final List<RoundScoreboard> roundScoreboards = seasonService.perRoundScoreboard(id);
//    TimeLogUtil.logFinish(startTime);
//    return roundScoreboards;
//  }

}
