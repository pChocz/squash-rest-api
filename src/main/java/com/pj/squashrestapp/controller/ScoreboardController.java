package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.Scoreboard;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.util.MatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/scoreboards")
public class ScoreboardController {

  @Autowired
  private MatchRepository matchRepository;

  @RequestMapping(
          value = "/bySeveralPlayers",
          params = {"ids"},
          method = GET)
  @ResponseBody
  Scoreboard bySeveralPlayers(
          @RequestParam final Long[] ids) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveBySeveralPlayersById(ids);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    final Scoreboard scoreboard = new Scoreboard(matches);
    return scoreboard;
  }

  @RequestMapping(
          value = "/byRoundGroupId",
          params = {"id"},
          method = GET)
  @ResponseBody
  Scoreboard byRoundId(
          @RequestParam("id") final Long id) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveByRoundGroupId(id);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    final Scoreboard scoreboard = new Scoreboard(matches);
    return scoreboard;
  }

  @RequestMapping(
          value = "/byLeagueId",
          params = {"id"},
          method = GET)
  @ResponseBody
  Scoreboard byLeagueId(
          @RequestParam("id") final Long id) {
    final List<SingleSetRowDto> sets = matchRepository.retrieveByLeagueId(id);
    final List<MatchDto> matches = MatchUtil.rebuildMatches(sets);
    final Scoreboard scoreboard = new Scoreboard(matches);
    return scoreboard;
  }

}
