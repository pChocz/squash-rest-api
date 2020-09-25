package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.service.BonusPointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/bonusPoints")
@RequiredArgsConstructor
public class BonusPointController {

  private final BonusPointService bonusPointService;


  @GetMapping
  @ResponseBody
  List<BonusPoint> extract(@RequestParam final UUID playerUuid,
                           @RequestParam final UUID seasonUuid) {
    final List<BonusPoint> bonusPoints = bonusPointService.extractBonusPoints(playerUuid, seasonUuid);
    return bonusPoints;
  }

  @PostMapping
  @ResponseBody
  List<BonusPoint> apply(@RequestParam final UUID winnerUuid,
                         @RequestParam final UUID looserUuid,
                         @RequestParam final UUID seasonUuid,
                         @RequestParam final int points) {
    final List<BonusPoint> bonusPoints = bonusPointService.applyBonusPointsForTwoPlayers(winnerUuid, looserUuid, seasonUuid, points);
    return bonusPoints;
  }

}
