package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.service.RoundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import java.time.LocalDate;
import java.util.Collection;
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
//  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  Long newRound(
          @RequestBody
          @RequestParam("roundNumber") final int roundNumber,
          @RequestParam("roundDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate roundDate,
          @RequestParam("seasonId") final Long seasonId,
          @RequestParam("playersIds") final List<Long[]> playersIds) {
    final Round round = roundService.createRound(roundNumber, roundDate, seasonId, playersIds);
    log.info("created round {}", round.getId());
    return round.getId();
  }

  @GetMapping("/backup/{roundId}")
  @ResponseBody
//  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  String backupRound(@PathVariable final Long roundId) {
    final String roundXml = roundService.backupRound(roundId);
    return roundXml;
  }

  // this one will be deleted later
//  @PostMapping
//  @ResponseBody
//  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
//  Round newRound(
//          @RequestBody
//          @RequestParam("roundNumber") final int roundNumber,
//          @RequestParam("roundDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate roundDate,
//          @RequestParam("seasonNumber") final int seasonNumber,
//          @RequestParam("leagueId") final Long leagueId,
//          @RequestParam("playersIds") final List<Long[]> playersIds) {
//    final Round round = roundService.createRound(roundNumber, roundDate, seasonNumber, leagueId, playersIds);
//    return round;
//  }


  @DeleteMapping(value = "/{roundId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
//  @PreAuthorize("hasRoleForRound(#roundId, 'MODERATOR')")
  void deleteRound(@PathVariable final Long roundId) {
    roundService.deleteRound(roundId);
    log.info("Round {} has been deleted", roundId);
  }


  /**
   * TEST - dummy method just to verify that testing of Spring Security works
   */
  @GetMapping(value = "dummyEndpoint/{value}")
  @PreAuthorize("isAdmin()")
  @ResponseBody
  int dummyGetEndpoint(@PathVariable final int value) {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    final String username = authentication.getName();
    final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    log.info("user info: \n USER:\t{} \n ROLES:\t{}", username, authorities);

    return value * 2;
  }

}
