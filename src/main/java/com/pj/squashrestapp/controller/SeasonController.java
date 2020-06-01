package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.RoundScoreboard;
import com.pj.squashrestapp.model.dto.SeasonScoreboardDto;
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
  SeasonScoreboardDto overalScoreboard(@RequestParam("id") final Long id) {
    final SeasonScoreboardDto seasonScoreboardDto = seasonService.overalScoreboard(id);
    return seasonScoreboardDto;
  }

  @RequestMapping(
          value = "/perRoundScoreboard",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<RoundScoreboard> perRoundScoreboard(@RequestParam("id") final Long id) {
    final List<RoundScoreboard> roundScoreboards = seasonService.perRoundScoreboard(id);
    return roundScoreboards;
  }

}
