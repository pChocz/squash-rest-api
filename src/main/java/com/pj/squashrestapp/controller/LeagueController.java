package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.LeagueDto;
import com.pj.squashrestapp.dto.PlayerDetailedDto;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.leaguestats.LeagueStatsWrapper;
import com.pj.squashrestapp.dto.leaguestats.OveralStats;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.service.LeagueService;
import com.pj.squashrestapp.service.RedisCacheService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/leagues")
@RequiredArgsConstructor
public class LeagueController {

  private final LeagueService leagueService;
  private final RedisCacheService redisCacheService;

  @PostMapping
  @ResponseBody
  UUID createNewLeague(
      @RequestParam final String leagueName,
      @RequestParam final String logoBase64,
      @RequestParam final int numberOfRounds,
      @RequestParam final int numberOfRoundsToBeDeducted,
      @RequestParam final MatchFormatType matchFormatType,
      @RequestParam final SetWinningType regularSetWinningType,
      @RequestParam final int regularSetWinningPoints,
      @RequestParam final SetWinningType tiebreakWinningType,
      @RequestParam final int tiebreakWinningPoints,
      @RequestParam(required = false) final String leagueWhen,
      @RequestParam(required = false) final String leagueWhere) {

    final UUID newLeagueUuid =
        leagueService.createNewLeague(
            leagueName,
            logoBase64,
            numberOfRounds,
            numberOfRoundsToBeDeducted,
            matchFormatType,
            regularSetWinningType,
            regularSetWinningPoints,
            tiebreakWinningType,
            tiebreakWinningPoints,
            leagueWhen,
            leagueWhere);

    return newLeagueUuid;
  }

  @DeleteMapping(value = "/{leagueUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void removeLeague(@PathVariable final UUID leagueUuid) {
    leagueService.removeLeague(leagueUuid);
    redisCacheService.clearAll();
  }

  @PutMapping(value = "/{leagueUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void changeLeagueLogo(
      @PathVariable final UUID leagueUuid, @RequestParam final String logoBase64) {
    leagueService.changeLogoForLeague(leagueUuid, logoBase64);
    redisCacheService.evictCacheForLeagueLogo(leagueUuid);
  }

  @GetMapping(value = "/general-info/{leagueUuid}")
  @ResponseBody
  LeagueDto extractLeagueGeneralInfo(@PathVariable final UUID leagueUuid) {
    final LeagueDto leagueGeneralInfo = leagueService.buildGeneralInfoForLeague(leagueUuid);
    return leagueGeneralInfo;
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
    final List<PlayerDto> playersGeneralInfo =
        leagueService.extractLeaguePlayersGeneral(leagueUuid);
    return playersGeneralInfo;
  }

  @GetMapping(value = "/players-detailed/{leagueUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  List<PlayerDetailedDto> playersDetailedByLeagueId(@PathVariable final UUID leagueUuid) {
    final List<PlayerDetailedDto> players = leagueService.extractLeaguePlayersDetailed(leagueUuid);
    return players;
  }

  @GetMapping(value = "/stats/{leagueUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
  LeagueStatsWrapper extractLeagueStatistics(@PathVariable final UUID leagueUuid) {
    final LeagueStatsWrapper leagueStatsWrapper = leagueService.buildStatsForLeagueUuid(leagueUuid);
    return leagueStatsWrapper;
  }

  @GetMapping(value = "/overal-stats/{leagueUuid}")
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'PLAYER')")
  OveralStats extractLeagueOveralStats(@PathVariable final UUID leagueUuid) {
    final OveralStats leagueOveralStats = leagueService.buildOveralStatsForLeagueUuid(leagueUuid);
    return leagueOveralStats;
  }

  @GetMapping(value = "/name-taken/{leagueName}")
  @ResponseBody
  boolean checkLeagueNameTaken(@PathVariable final String leagueName) {
    final boolean isLeagueNameAvailable = leagueService.checkLeagueNameTaken(leagueName);
    return isLeagueNameAvailable;
  }
}
