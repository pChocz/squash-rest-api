package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

  private final MatchService matchService;


  @PutMapping(value = "/{matchUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("""
          hasRoleForMatch(#matchUuid, 'MODERATOR')
          or
          (hasRoleForMatch(#matchUuid, 'PLAYER') and isRoundOfMatchInProgress(#matchUuid))
          """)
  void updateSingleScore(@PathVariable final UUID matchUuid,
                         @RequestParam final int setNumber,
                         @RequestParam final String player,
                         @RequestParam final Integer newScore) {

    matchService.modifySingleScore(matchUuid, setNumber, player, newScore);
  }


  @GetMapping(value = "/for-league-for-players/{leagueUuid}/{playersUuids}")
  @ResponseBody
  MatchesSimplePaginated matchesPageable(@PageableDefault(sort = {"id", "number"}, direction = Sort.Direction.DESC) final Pageable pageable,
                                         @PathVariable final UUID leagueUuid,
                                         @PathVariable final UUID[] playersUuids,
                                         @RequestParam(required = false) final UUID seasonUuid,
                                         @RequestParam(required = false) final Integer groupNumber) {

    final MatchesSimplePaginated matchesPaginated = matchService.getRoundMatchesPaginated(pageable, leagueUuid, playersUuids, seasonUuid, groupNumber);
    return matchesPaginated;
  }

  @GetMapping(value = "/for-league-for-players-additional/{leagueUuid}/{playersUuids}")
  @ResponseBody
  MatchesSimplePaginated matchesAdditionalPageable(@PageableDefault(sort = {"id", "date"}, direction = Sort.Direction.DESC) final Pageable pageable,
                                                   @PathVariable final UUID leagueUuid,
                                                   @PathVariable final UUID[] playersUuids,
                                                   @RequestParam(required = false) final UUID seasonUuid) {

    final MatchesSimplePaginated matchesPaginated = matchService.getAdditionalMatchesPaginated(pageable, leagueUuid, playersUuids, seasonUuid);
    return matchesPaginated;
  }

}
