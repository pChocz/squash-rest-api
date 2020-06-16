package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.entityhelper.MatchHelper;
import com.pj.squashrestapp.model.entityhelper.SetResultHelper;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
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
  private MatchRepository matchRepository;

  @Autowired
  private SetResultRepository setResultRepository;


  @GetMapping(value = "/{matchId}")
  @ResponseBody
  MatchDto getMatch(@PathVariable final Long matchId) {
    final long startTime = System.nanoTime();
    final Match match = matchRepository.findMatchById(matchId);
    final MatchDto matchDto = new MatchDto(match);
    TimeLogUtil.logFinishWithJsonPrint(startTime, matchDto);
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
    final long startTime = System.nanoTime();

    final Match matchToModify = matchRepository.findMatchById(matchId);

    final String initialMatchResult = matchToModify.toString();
    final SetResult setToModify = matchToModify.getSetResults().stream().filter(set -> set.getNumber() == setNumber).findFirst().orElse(null);

    try {

      if (setToModify.getFirstPlayerScore() != 0
          || setToModify.getSecondPlayerScore() != 0) {
        throw new IllegalArgumentException();
      }

      setToModify.setFirstPlayerScore(p1score);
      setToModify.setSecondPlayerScore(p2score);

      final SetResultHelper setResultHelper = new SetResultHelper(setToModify);
      if (!setResultHelper.isValid()) {
        throw new IllegalArgumentException();
      }

      final MatchHelper matchHelper = new MatchHelper(matchToModify);
      if (!matchHelper.isValid()) {
        throw new IllegalArgumentException();
      }

      setResultRepository.save(setToModify);

      final String message = "\nSuccesfully updated the match!" +
                             "\n\t-> " + initialMatchResult + "\t- earlier" +
                             "\n\t-> " + matchToModify + "\t- now";
      log.info(message);


    } catch (final IllegalArgumentException e) {
      final String message = "\nDoes not look like a valid match result after the update!" +
                             "\n\t-> " + matchToModify + "\t- tried to update to look like this" +
                             "\n\t-> " + initialMatchResult + "\t- leaving the old result like this.";
      log.error(message);
    }

    final MatchDto matchDto = new MatchDto(matchToModify);
    TimeLogUtil.logFinishWithJsonPrint(startTime, matchDto);
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
    final long startTime = System.nanoTime();

    final Match matchToModify = matchRepository.findMatchById(matchId);

    final SetResult setToModify = matchToModify
            .getSetResults()
            .stream()
            .filter(set -> set.getNumber() == setNumber)
            .findFirst()
            .orElse(null);

    final String initialMatchResult = matchToModify.toString();

    setToModify.setFirstPlayerScore(0);
    setToModify.setSecondPlayerScore(0);

    setResultRepository.save(setToModify);

    final String message = "\nSuccesfully updated the match!" +
                           "\n\t-> " + initialMatchResult + "\t- earlier" +
                           "\n\t-> " + matchToModify + "\t- now";
    log.info(message);

    final MatchDto matchDto = new MatchDto(matchToModify);
    TimeLogUtil.logFinishWithJsonPrint(startTime, matchDto);
    return matchDto;
  }

}
