package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.dto.LeagueDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.util.TimeLogUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

  @PostMapping(value = "")
  @ResponseBody
  void createNewLeague(@RequestParam("leagueName") final String leagueName) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    final Player player = (Player) auth.getPrincipal();
    leagueService.createNewLeague(leagueName, player);
  }

  @GetMapping(value = "/general-info")
  @ResponseBody
  List<LeagueDto> extractLeagueStatistics() {
    final long startTime = System.nanoTime();
    final List<LeagueDto> allLeaguesGeneralInfo = leagueService.buildGeneralInfoForAllLeagues();
    TimeLogUtil.logFinish(startTime);
    return allLeaguesGeneralInfo;
  }

  @GetMapping(value = "/{leagueId}/players-general")
  @ResponseBody
  List<PlayerDto> playersGeneralByLeagueId(
          @PathVariable("leagueId") final Long leagueId) {

    final List<PlayerDto> playersGeneralInfo = leagueService.extractLeaguePlayersGeneral(leagueId);
    return playersGeneralInfo;
  }

  @ApiOperation(value = "Extract league statistics", authorizations = {@Authorization(value = "jwtToken")})
  @GetMapping(value = "/{leagueId}/stats")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'PLAYER')")
  LeagueStatsWrapper extractLeagueStatistics(@PathVariable final Long leagueId) {
    final long startTime = System.nanoTime();
    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueId(leagueId);
    TimeLogUtil.logFinish(startTime);
    return leagueStatsWrapper;
  }

  @ApiOperation(value = "Update league logo", authorizations = {@Authorization(value = "jwtToken")})
  @PutMapping(value = "/{leagueId}/logo")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  void updateLeagueLogo(@PathVariable final Long leagueId,
                          @RequestParam("file") final MultipartFile file) throws IOException {
    final byte[] logoBytes = file.getBytes();
    leagueService.saveLogoForLeague(leagueId, logoBytes);
  }

}
