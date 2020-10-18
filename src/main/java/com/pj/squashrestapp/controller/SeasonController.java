package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.PlayerDto;
import com.pj.squashrestapp.model.dto.SeasonDto;
import com.pj.squashrestapp.service.SeasonService;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/seasons")
@RequiredArgsConstructor
public class SeasonController {

  private final SeasonService seasonService;


  @PostMapping
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#leagueUuid, 'MODERATOR')")
  SeasonDto createNewSeason(@RequestParam final int seasonNumber,
                            @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_FORMAT) final LocalDate startDate,
                            @RequestParam final UUID leagueUuid) {

    final Season season = seasonService.createNewSeason(seasonNumber, startDate, leagueUuid);
    return new SeasonDto(season);
  }


  @GetMapping(value = "/{seasonUuid}")
//  @PreAuthorize("isAdmin()")
  @ResponseBody
  SeasonDto extractSeasonDto(@PathVariable final UUID seasonUuid) {
    final SeasonDto seasonDto = seasonService.extractSeasonDtoByUuid(seasonUuid);
    return seasonDto;
  }

  @GetMapping(value = "/{seasonUuid}/players")
  @ResponseBody
  List<PlayerDto> extractSeasonPlayers(@PathVariable final UUID seasonUuid) {
    final List<PlayerDto> seasonPlayers = seasonService.extractSeasonPlayers(seasonUuid);
    return seasonPlayers;
  }

  @DeleteMapping(value = "/{seasonUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRoleForRound(#seasonUuid, 'MODERATOR')")
  void deleteRound(@PathVariable final UUID seasonUuid) {
    seasonService.deleteSeason(seasonUuid);
    log.info("Season {} has been deleted", seasonUuid);
  }

}
