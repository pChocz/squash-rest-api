package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.service.MatchService;
import lombok.extern.slf4j.Slf4j;
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

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/matches")
public class MatchController {

  private final MatchService matchService;

  public MatchController(final MatchService matchService) {
    this.matchService = matchService;
  }


  @GetMapping(value = "/{matchId}")
  @ResponseBody
  MatchDto getMatch(@PathVariable final Long matchId) {
    final MatchDto matchDto = matchService.getMatch(matchId);
    return matchDto;
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
  MatchDto updateFinishedMatch(
          @PathVariable("matchId") final Long matchId,
          @PathVariable("setNumber") final int setNumber,
          @RequestParam("p1score") final int p1score,
          @RequestParam("p2score") final int p2score) {

    final MatchDto matchDto = matchService.modifyMatch(matchId, setNumber, p1score, p2score);
    return matchDto;
  }


  @DeleteMapping(value = "/{matchId}/set/{setNumber}")
  @ResponseBody
  @PreAuthorize("hasRoleForMatch(#matchId, 'MODERATOR') " +
                "or " +
                "(hasRoleForMatch(#matchId, 'PLAYER') and isRoundOfMatchInProgress(#matchId))")
  MatchDto clearSet(
          @PathVariable("matchId") final Long matchId,
          @PathVariable("setNumber") final int setNumber) {

    final MatchDto matchDto = matchService.clearSingleSetOfMatch(matchId, setNumber);
    return matchDto;
  }

}
