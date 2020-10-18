package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.dto.BonusPointsDto;
import com.pj.squashrestapp.service.BonusPointService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/bonusPoints")
@RequiredArgsConstructor
public class BonusPointController {

  private final BonusPointService bonusPointService;


  @GetMapping("/season/player")
  @ResponseBody
  List<BonusPoint> extractForSeasonForPlayer(@RequestParam final UUID playerUuid,
                                             @RequestParam final UUID seasonUuid) {
    final List<BonusPoint> bonusPoints = bonusPointService.extractBonusPoints(playerUuid, seasonUuid);
    return bonusPoints;
  }

  @GetMapping("/season/{seasonUuid}")
  @ResponseBody
  List<BonusPointsDto> extractForSeason(@PathVariable final UUID seasonUuid) {
    final List<BonusPoint> bonusPoints = bonusPointService.extractBonusPoints(seasonUuid);
    final List<BonusPointsDto> bonusPointsForSeason = bonusPoints
            .stream()
            .map(BonusPointsDto::new)
            .collect(Collectors.toList());
    return bonusPointsForSeason;
  }

  @PostMapping
  @ResponseBody
  List<BonusPoint> apply(@RequestParam final UUID winnerUuid,
                         @RequestParam final UUID looserUuid,
                         @RequestParam final UUID seasonUuid,
                         @RequestParam final int points) {
    final List<BonusPoint> bonusPoints = bonusPointService.applyBonusPointsForTwoPlayers(winnerUuid, looserUuid, seasonUuid, points);
    TimeLogUtil.logMessage("ALL-AGAINST-ALL stats: ");
    return bonusPoints;
  }

}
