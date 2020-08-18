package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.service.BonusPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@CrossOrigin(origins = "http://localhost:4200")
public class BonusPointController {

  @Autowired
  private BonusPointService bonusPointService;

  @GetMapping(value = "/seasons/{seasonId}/players/{playerId}")
  @ResponseBody
  List<BonusPoint> forSeasonForPlayer(@PathVariable final Long seasonId,
                                      @PathVariable final Long playerId) {
    final List<BonusPoint> bonusPoints = bonusPointService.extractBonusPoints(playerId, seasonId);
    return bonusPoints;
  }

  @PostMapping(
          value = "/apply",
          params = {"winnerId", "looserId", "seasonId", "points"})
  @ResponseBody
  List<BonusPoint> applyBonusPoints(@RequestParam("winnerId") final Long winnerId,
                                    @RequestParam("looserId") final Long looserId,
                                    @RequestParam("seasonId") final Long seasonId,
                                    @RequestParam("points") final int points) {
    final List<BonusPoint> bonusPoints = bonusPointService.applyPoints(winnerId, looserId, seasonId, points);
    return bonusPoints;
  }

}
