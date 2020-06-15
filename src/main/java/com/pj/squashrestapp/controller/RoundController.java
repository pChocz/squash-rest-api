package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/rounds")
public class RoundController {

  @Autowired
  private RoundService roundService;

  /**
   * EXAMPLE:
   *  addRoundToSeason ? roundNumber=9 & seasonNumber=5 & leagueId=1 & playersIds=1,2,3 & playersIds=4,5,6
   *
   */
  @RequestMapping(
          value = "/add",
          params = {"roundNumber", "roundDate", "seasonNumber", "leagueId", "playersIds"},
          method = POST)
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
  @ResponseBody
  @PreAuthorize("hasRoleForRound(#roundId, 'MODERATOR')")
  String delete(@PathVariable final Long roundId) {
    roundService.deleteRound(roundId);
    return "Round " + roundId + " deleted";
  }

}
