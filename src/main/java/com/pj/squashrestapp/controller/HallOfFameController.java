package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.TrophiesWonForLeague;
import com.pj.squashrestapp.service.HallOfFameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/hall-of-fame")
@RequiredArgsConstructor
public class HallOfFameController {

  private final HallOfFameService hallOfFameService;

  @GetMapping(value = "/{playerUuid}")
  @ResponseBody
  List<TrophiesWonForLeague> extractHallOfFameForPlayer(@PathVariable final UUID playerUuid) {
    final List<TrophiesWonForLeague> trophiesWonForLeagues = hallOfFameService.extractHallOfFameForPlayer(playerUuid);
    return trophiesWonForLeagues;
  }

}
