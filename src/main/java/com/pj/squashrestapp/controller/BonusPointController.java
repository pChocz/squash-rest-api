package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.service.BonusPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/bonusPoints")
public class BonusPointController {

  @Autowired
  private BonusPointService bonusPointService;


  @RequestMapping(
          value = "/forPlayer",
          params = {"playerId", "seasonId"},
          method = GET)
  @ResponseBody
  List<BonusPoint> forPlayer(@RequestParam("playerId") final Long playerId,
                             @RequestParam("seasonId") final Long seasonId) {
    final List<BonusPoint> bonusPoints = bonusPointService.extractBonusPoints(playerId, seasonId);
    return bonusPoints;
  }

  @RequestMapping(
          value = "/apply",
          params = {"winnerId", "looserId", "seasonId", "points"},
          method = POST)
  @ResponseBody
  List<BonusPoint> applyBonusPoints(@RequestParam("winnerId") final Long winnerId,
                                    @RequestParam("looserId") final Long looserId,
                                    @RequestParam("seasonId") final Long seasonId,
                                    @RequestParam("points") final int points) {
    final List<BonusPoint> bonusPoints = bonusPointService.applyPoints(winnerId, looserId, seasonId, points);
    return bonusPoints;
  }

}
