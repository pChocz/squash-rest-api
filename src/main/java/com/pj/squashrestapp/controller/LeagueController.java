package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.QueryLog;
import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.service.LeagueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.server.ResponseStatusException;

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


  @PostMapping
  @ResponseBody
  LeagueDto createNewLeague(@RequestParam final String leagueName) {
    final LeagueDto leagueDto = leagueService.createNewLeague(leagueName);
    return leagueDto;
  }


  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void removeLeague(@RequestParam final UUID leagueUuid) {
    leagueService.removeEmptyLeague(leagueUuid);
  }


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

}
