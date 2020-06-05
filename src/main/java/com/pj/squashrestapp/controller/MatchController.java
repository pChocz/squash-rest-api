package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.SetDto;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import com.pj.squashrestapp.model.entityhelper.SetResultHelper;
import com.pj.squashrestapp.model.projection.MatchProjection;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.MatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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

  @RequestMapping(
          value = "/bySinglePlayer",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<MatchDto> bySinglePlayer(
          @RequestParam("id") final Long id) {
//    final List<MatchProjection> matchProjections = matchRepository.retrieveAllMatchesProjection();
    final List<SingleSetRowDto> sets = matchRepository.retrieveBySinglePlayer(id);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    return matches;
  }

  @RequestMapping(
          value = "/bySeveralPlayers",
          params = {"ids"},
          method = GET)
  @ResponseBody
  List<MatchDto> bySeveralPlayers(
          @RequestParam final Long[] ids) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveBySeveralPlayersById(ids);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    return matches;
  }

  @RequestMapping(
          value = "/byRoundGroupId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<MatchDto> byRoundId(
          @RequestParam("id") final Long id) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveByRoundGroupId(id);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    return matches;
  }

  @RequestMapping(
          value = "/bySeasonId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<MatchDto> bySeasonId(
          @RequestParam("id") final Long id) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveBySeasonId(id);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    return matches;
  }

  @RequestMapping(
          value = "/byLeagueId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<MatchDto> byLeagueId(
          @RequestParam("id") final Long id) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveByLeagueId(id);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    return matches;
  }

  @RequestMapping(
          value = "/byMatchId",
          params = {"id"},
          method = GET)
  @ResponseBody
  MatchDto byMatchId(
          @RequestParam("id") final Long id) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveByMatchId(id);
    final MatchDto match = MatchUtil.rebuildSingleMatch(sets);
    return match;
  }


  /**
   * EXAMPLE:
   *  localhost:8080/matches/updateMatch?matchId=402&setNumber=1&p1score=11&p2score=4
   *
   */
  @RequestMapping(
          value = "/updateFinishedMatch",
          params = {"matchId", "setNumber", "p1score", "p2score"},
          method = POST)
  @ResponseBody
  @PreAuthorize("hasRoleForMatch(#matchId, 'MODERATOR')")
  SetDto updateFinishedMatch(
          @RequestParam("matchId") final Long matchId,
          @RequestParam("setNumber") final int setNumber,
          @RequestParam("p1score") final int p1score,
          @RequestParam("p2score") final int p2score) {
    final SetResult setToModify = setResultRepository.findByMatchIdAndNumber(matchId, setNumber);
    setToModify.setFirstPlayerScore(p1score);
    setToModify.setSecondPlayerScore(p2score);

    final SetResultHelper setResultHelper = new SetResultHelper(setToModify);
    if (!setResultHelper.isValid()) {
      throw new IllegalArgumentException("Not valid set result provided! -> " + setToModify);
    }

    setResultRepository.save(setToModify);
    return new SetDto(setToModify);
  }


  /**
   * EXAMPLE:
   *  localhost:8080/matches/updateMatch?matchId=402&setNumber=1&p1score=11&p2score=4
   *
   */
  @RequestMapping(
          value = "/updateMatch",
          params = {"matchId", "setNumber", "p1score", "p2score"},
          method = POST)
  @ResponseBody
  @PreAuthorize("hasRoleForMatch(#matchId, 'PLAYER') and isMatchEmpty(#matchId)")
  SetDto updateMatch(
          @RequestParam("matchId") final Long matchId,
          @RequestParam("setNumber") final int setNumber,
          @RequestParam("p1score") final int p1score,
          @RequestParam("p2score") final int p2score) {
    final SetResult setToModify = setResultRepository.findByMatchIdAndNumber(matchId, setNumber);
    setToModify.setFirstPlayerScore(p1score);
    setToModify.setSecondPlayerScore(p2score);

    final SetResultHelper setResultHelper = new SetResultHelper(setToModify);
    if (!setResultHelper.isValid()) {
      throw new IllegalArgumentException("Not valid set result provided! -> " + setToModify);
    }

    setResultRepository.save(setToModify);
    return new SetDto(setToModify);
  }



}
