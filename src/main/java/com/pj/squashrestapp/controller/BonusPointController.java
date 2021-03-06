package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.BonusPointsDto;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.service.BonusPointService;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/bonus-points")
@RequiredArgsConstructor
public class BonusPointController {

  private final BonusPointService bonusPointService;

  @PostMapping
  @ResponseBody
  @PreAuthorize("hasRoleForSeason(#seasonUuid, 'PLAYER')")
  BonusPoint apply(
      @RequestParam final UUID winnerUuid,
      @RequestParam final UUID looserUuid,
      @RequestParam final UUID seasonUuid,
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_FORMAT) final LocalDate date,
      @RequestParam final int points) {
    final BonusPoint bonusPoint =
        bonusPointService.applyBonusPointsForTwoPlayers(
            winnerUuid, looserUuid, seasonUuid, date, points);
    return bonusPoint;
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForBonusPoint(#uuid, 'MODERATOR')")
  void apply(@PathVariable final UUID uuid) {
    bonusPointService.deleteBonusPoint(uuid);
  }

  @GetMapping("/seasons/{seasonUuid}")
  @ResponseBody
  List<BonusPointsDto> extractForSeason(@PathVariable final UUID seasonUuid) {
    final List<BonusPoint> bonusPoints = bonusPointService.extractBonusPoints(seasonUuid);
    final List<BonusPointsDto> bonusPointsForSeason =
        bonusPoints.stream().map(BonusPointsDto::new).collect(Collectors.toList());
    return bonusPointsForSeason;
  }
}
