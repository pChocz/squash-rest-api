package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.RoundScoreboard;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/round-scoreboard")
public class RoundScoreboardController {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private SetResultRepository setResultRepository;

  @Autowired
  private XpPointsRepository xpPointsRepository;

  @RequestMapping(
          value = "/byRoundId",
          params = {"id"},
          method = GET)
  @ResponseBody
  RoundScoreboard byRoundId(@RequestParam("id") final Long id) {

    final List<SetResult> setResults = setResultRepository.fetchByRoundId(id);
    final Round round = EntityGraphBuildUtil.reconstructRound(setResults, id);

    final RoundScoreboard roundScoreboard = new RoundScoreboard();
    for (final RoundGroup roundGroup : round.getRoundGroups()) {
      roundScoreboard.addRoundGroup(roundGroup.getMatches().stream().map(MatchDto::new).collect(Collectors.toList()));
    }

    final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
    final String split = GeneralUtil.integerListToString(playersPerGroup);
    final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplit(split);

    roundScoreboard.assignPointsAndPlaces(xpPoints);
    return roundScoreboard;
  }

}
