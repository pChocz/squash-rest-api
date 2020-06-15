package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.SetDto;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.entityhelper.MatchHelper;
import com.pj.squashrestapp.model.entityhelper.SetResultHelper;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.TimeLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
          params = {"playerId", "leagueId"},
          method = GET)
  @ResponseBody
  List<MatchDto> bySinglePlayer(
          @RequestParam("playerId") final Long playerId,
          @RequestParam("leagueId") final Long leagueId) {
    final long startTime = System.nanoTime();

    final List<SetResult> setResults = setResultRepository.fetchByOnePlayerIdAndLeagueId(leagueId, playerId);
    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, leagueId);
    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);

    TimeLogUtil.logFinishWithJsonPrint(startTime, matches);
    return matches;
  }

  @RequestMapping(
          value = "/bySeveralPlayers",
          params = {"playersIds", "leagueId"},
          method = GET)
  @ResponseBody
  List<MatchDto> bySeveralPlayers(
          @RequestParam("playersIds") final Long[] playersIds,
          @RequestParam("leagueId") final Long leagueId) {
    final long startTime = System.nanoTime();

    final List<SetResult> setResults = setResultRepository.fetchBySeveralPlayersIdsAndLeagueId(leagueId, playersIds);
    final League leagueFetched = EntityGraphBuildUtil.reconstructLeague(setResults, leagueId);
    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(leagueFetched);

    TimeLogUtil.logFinishWithJsonPrint(startTime, matches);
    return matches;
  }

  @RequestMapping(
          value = "/byRoundGroupId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<MatchDto> byRoundId(
          @RequestParam("id") final Long id) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundGroupId(id);
    final RoundGroup roundGroup = EntityGraphBuildUtil.reconstructRoundGroup(setResults, id);
    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(roundGroup);
    return matches;
  }

  @RequestMapping(
          value = "/bySeasonId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<MatchDto> bySeasonId(
          @RequestParam("id") final Long id) {
    final List<SetResult> setResults = setResultRepository.fetchBySeasonId(id);
    final RoundGroup roundGroup = EntityGraphBuildUtil.reconstructRoundGroup(setResults, id);
    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(roundGroup);
    return matches;
  }

  @RequestMapping(
          value = "/byLeagueId",
          params = {"id"},
          method = GET)
  @ResponseBody
  List<MatchDto> byLeagueId(
          @RequestParam("id") final Long id) {
    final List<SetResult> setResults = setResultRepository.fetchByLeagueId(id);
    final RoundGroup roundGroup = EntityGraphBuildUtil.reconstructRoundGroup(setResults, id);
    final List<MatchDto> matches = MatchExtractorUtil.extractAllMatches(roundGroup);
    return matches;
  }

  @RequestMapping(
          value = "/byMatchId",
          params = {"id"},
          method = GET)
  @ResponseBody
  MatchDto byMatchId(
          @RequestParam("id") final Long id) {
    final List<SetResult> setResults = setResultRepository.fetchByMatchId(id);
    final Match match = EntityGraphBuildUtil.reconstructMatch(setResults, id);
    final MatchDto matchDto = new MatchDto(match);
    return matchDto;
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
  @PreAuthorize("hasRoleForMatch(#matchId, 'MODERATOR') " +
          "or " +
          "(hasRoleForMatch(#matchId, 'PLAYER') and isRoundOfMatchInProgress(#matchId))")
  Object updateFinishedMatch(
          @RequestParam("matchId") final Long matchId,
          @RequestParam("setNumber") final int setNumber,
          @RequestParam("p1score") final int p1score,
          @RequestParam("p2score") final int p2score) {

    final Match matchToModify = matchRepository.getOne(matchId);
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

      return new SetDto(setToModify);

    } catch (final IllegalArgumentException e) {
      final String message = "\nDoes not look like a valid match result after the update!" +
              "\n\t-> " + matchToModify + "\t- tried to update to look like this" +
              "\n\t-> " + initialMatchResult + "\t- leaving the old result like this.";
      log.error(message);
      return message;
    }
  }

  @RequestMapping(
          value = "/clearSet",
          params = {"matchId", "setNumber"},
          method = POST)
  @ResponseBody
  @PreAuthorize("hasRoleForMatch(#matchId, 'MODERATOR') " +
          "or " +
          "(hasRoleForMatch(#matchId, 'PLAYER') and isRoundOfMatchInProgress(#matchId))")
  SetDto clearSet(
          @RequestParam("matchId") final Long matchId,
          @RequestParam("setNumber") final int setNumber) {
    final Match matchToModify = matchRepository.getOne(matchId);

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

    return new SetDto(setToModify);
  }

}
