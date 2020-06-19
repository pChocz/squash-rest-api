package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.service.RoundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/rounds")
public class RoundController {

  @Autowired
  private RoundService roundService;


  @PostMapping
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  Round newRound(
          @RequestBody
          @RequestParam("roundNumber") final int roundNumber,
          @RequestParam("roundDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate roundDate,
          @RequestParam("seasonNumber") final int seasonNumber,
          @RequestParam("leagueId") final Long leagueId,
          @RequestParam("playersIds") final List<Long[]> playersIds) {
    final Round round = roundService.createRound(roundNumber, roundDate, seasonNumber, leagueId, playersIds);
    return round;
  }


  @DeleteMapping(value = "/{roundId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForRound(#roundId, 'MODERATOR')")
  void deleteRound(@PathVariable final Long roundId) {
    roundService.deleteRound(roundId);
    log.info("Round {} has been deleted", roundId);
  }


  @GetMapping(value = "dummyEndpoint/{booleanValue}")
  @PreAuthorize("isAdmin()")
  @ResponseBody
  boolean dummyGetEndpoint(@PathVariable final boolean booleanValue) throws IOException {
    return booleanValue;
  }

}
