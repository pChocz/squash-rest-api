package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/leagues")
@CrossOrigin(origins = "http://localhost:4200")
public class LeagueController {

  @Autowired
  private LeagueService leagueService;

//  @Autowired
//  private LeagueAssembler leagueAssembler;
//
//  @RequestMapping(
//          value = "/entire",
//          params = {"id"},
//          method = GET)
//  @ResponseBody
//  public ResponseEntity<LeagueModel> entireLeague(@RequestParam("id") final Long id) {
//    final long startTime = System.nanoTime();
//
//    final League league = leagueService.fetchEntireLeague(id);
//    final LeagueModel leagueModel = leagueAssembler.toModel(league);
//    final ResponseEntity<LeagueModel> responseEntity = ResponseEntity.ok(leagueModel);
//
//    TimeLogUtil.logFinish(startTime);
//    return responseEntity;
//  }

  @GetMapping(value = "{leagueId}/stats")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'PLAYER')")
  LeagueStatsWrapper leagueStats(@PathVariable final Long leagueId) {
    final long startTime = System.nanoTime();
    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueId(leagueId);
    TimeLogUtil.logFinishWithJsonPrint(startTime, leagueStatsWrapper);
    return leagueStatsWrapper;
  }

}
