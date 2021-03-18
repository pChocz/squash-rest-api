package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.dto.TrophiesWonForLeague;
import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.service.LeagueTrophiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/trophies")
@RequiredArgsConstructor
public class LeagueTrophiesController {

  private final LeagueTrophiesService leagueTrophiesService;


  @PostMapping
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  TrophyForLeague addTrophy(@RequestParam final UUID playerUuid,
                            @RequestParam final UUID leagueUuid,
                            @RequestParam final int seasonNumber,
                            @RequestParam final Trophy trophy) {
    final TrophyForLeague trophyForLeague = leagueTrophiesService.addNewTrophy(playerUuid, leagueUuid, seasonNumber, trophy);
    return trophyForLeague;
  }


  @DeleteMapping
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  void removeTrophy(@RequestParam final UUID playerUuid,
                    @RequestParam final UUID leagueUuid,
                    @RequestParam final int seasonNumber,
                    @RequestParam final Trophy trophy) {
    leagueTrophiesService.removeTrophy(playerUuid, leagueUuid, seasonNumber, trophy);
  }


  @GetMapping(value = "/{playerUuid}")
  @ResponseBody
  List<TrophiesWonForLeague> extractHallOfFameForPlayer(@PathVariable final UUID playerUuid) {
    final List<TrophiesWonForLeague> trophiesWonForLeagues = leagueTrophiesService.extractTrophiesForPlayer(playerUuid);
    return trophiesWonForLeagues;
  }

}
