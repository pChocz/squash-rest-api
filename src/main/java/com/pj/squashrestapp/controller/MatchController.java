package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


  @GetMapping(value = "/pageable/leagues/{leagueUuid}/players/{playersIds}")
  @ResponseBody
  MatchesSimplePaginated matchesPageable(
          @PageableDefault(sort = {"id", "number"}, direction = Sort.Direction.DESC) final Pageable pageable,
          @PathVariable final UUID leagueUuid,
          @PathVariable final Long[] playersIds) {

    final MatchesSimplePaginated matchesPaginated =
            playersIds.length == 1
                    ? matchService.getMatchesPaginatedForOnePlayer(pageable, leagueUuid, playersIds[0])
                    : matchService.getMatchesPaginatedForMultiplePlayers(pageable, leagueUuid, playersIds);

    return matchesPaginated;
  }

  @GetMapping(value = "/{matchId}")
  @ResponseBody
  MatchDetailedDto getMatch(@PathVariable final Long matchId) {
    final MatchDetailedDto matchDetailedDto = matchService.getMatch(matchId);
    return matchDetailedDto;
  }


  @PutMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
//  @PreAuthorize("hasRoleForMatch(#matchId, 'MODERATOR') " +
//                "or " +
//                "(hasRoleForMatch(#matchId, 'PLAYER') and isRoundOfMatchInProgress(#matchId))")
  void updateSingleScore(
          @RequestBody
          @RequestParam("matchId") final Long matchId,
          @RequestParam("setNumber") final int setNumber,
          @RequestParam("player") final String player,
          @RequestParam("newScore") final Integer newScore) {

    matchService.modifySingleScore(matchId, setNumber, player, newScore);
  }


  @PutMapping(value = "/{matchId}/set/{setNumber}")
  @ResponseBody
  @PreAuthorize("hasRoleForMatch(#matchId, 'MODERATOR') " +
                "or " +
                "(hasRoleForMatch(#matchId, 'PLAYER') and isRoundOfMatchInProgress(#matchId))")
  MatchDetailedDto updateFinishedMatch(
          @PathVariable("matchId") final Long matchId,
          @PathVariable("setNumber") final int setNumber,
          @RequestParam("p1score") final int p1score,
          @RequestParam("p2score") final int p2score) {

    final MatchDetailedDto matchDetailedDto = matchService.modifyMatch(matchId, setNumber, p1score, p2score);
    return matchDetailedDto;
  }


  @DeleteMapping(value = "/{matchId}/set/{setNumber}")
  @ResponseBody
  @PreAuthorize("hasRoleForMatch(#matchId, 'MODERATOR') " +
                "or " +
                "(hasRoleForMatch(#matchId, 'PLAYER') and isRoundOfMatchInProgress(#matchId))")
  MatchDetailedDto clearSet(
          @PathVariable("matchId") final Long matchId,
          @PathVariable("setNumber") final int setNumber) {

    final MatchDetailedDto matchDetailedDto = matchService.clearSingleSetOfMatch(matchId, setNumber);
    return matchDetailedDto;
  }

}
