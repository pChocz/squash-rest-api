package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.entityhelper.MatchHelper;
import com.pj.squashrestapp.model.entityhelper.SetResultHelper;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.service.MatchService;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/matches")
public class MatchController {

  @Autowired
  private MatchService matchService;


  @GetMapping(value = "/{matchId}")
  @ResponseBody
  MatchDto getMatch(@PathVariable final Long matchId) {
    final MatchDto matchDto = matchService.getMatch(matchId);
    return matchDto;
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
