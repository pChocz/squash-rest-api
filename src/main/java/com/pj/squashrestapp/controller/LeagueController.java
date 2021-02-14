package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.QueryLog;
import com.pj.squashrestapp.model.dto.LeagueDto;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.service.LeagueService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/leagues")
@RequiredArgsConstructor
public class LeagueController {

  private final LeagueService leagueService;


  @GetMapping(value = "/general-info/{leagueUuid}")
  @ResponseBody
  LeagueDto extractLeagueGeneralInfo(@PathVariable final UUID leagueUuid) {
    try {
      final LeagueDto leagueGeneralInfo = leagueService.buildGeneralInfoForLeague(leagueUuid);
      return leagueGeneralInfo;

    } catch (final NoSuchElementException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League has not been found!");
    }
  }


  @GetMapping(value = "/general-info")
  @ResponseBody
  List<LeagueDto> extractAllLeaguesGeneralInfo() {
    final List<LeagueDto> allLeaguesGeneralInfo = leagueService.buildGeneralInfoForAllLeagues();
    return allLeaguesGeneralInfo;
  }


  @GetMapping(value = "/all-logos")
  @ResponseBody
  Map<UUID, byte[]> extractAllLeaguesLogosMap() {
    final Map<UUID, byte[]> allLeaguesLogos = leagueService.extractAllLogos();
    return allLeaguesLogos;
  }


  @GetMapping(value = "/players/{leagueUuid}")
  @ResponseBody
  List<PlayerDto> playersGeneralByLeagueId(@PathVariable final UUID leagueUuid) {
    final List<PlayerDto> playersGeneralInfo = leagueService.extractLeaguePlayersGeneral(leagueUuid);
    return playersGeneralInfo;
  }


  @GetMapping(value = "/stats/{leagueUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
  @QueryLog
  LeagueStatsWrapper extractLeagueStatistics(@PathVariable final UUID leagueUuid) {
    try {
      final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueUuid(leagueUuid);
      return leagueStatsWrapper;

    } catch (final NoSuchElementException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "League has not been found!");
    }
  }


  @ApiOperation(value = "Update league logo", authorizations = {@Authorization(value = "jwtToken")})
  @PutMapping(value = "/logo/{leagueUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void updateLeagueLogo(@PathVariable final UUID leagueUuid,
                        @RequestParam final MultipartFile file) throws IOException {
    final byte[] logoBytes = file.getBytes();
    leagueService.saveLogoForLeague(leagueUuid, logoBytes);
  }

}
