package com.pj.squashrestapp.controller;

import com.google.common.collect.Multimap;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.RoundScoreboard;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.MatchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
  private XpPointsRepository xpPointsRepository;

  @RequestMapping(
          value = "/byRoundId",
          params = {"id"},
          method = GET)
  @ResponseBody
  RoundScoreboard byRoundId(@RequestParam("id") final Long id) {

    final List<SingleSetRowDto> sets = matchRepository.retrieveByRoundId(id);
    final Multimap<Long, MatchDto> perGroupMatches = MatchUtil.rebuildRoundMatchesPerRoundGroupId(sets);

    final RoundScoreboard roundScoreboard = new RoundScoreboard();
    for (final Long roundGroupId : perGroupMatches.keySet()) {
      roundScoreboard.addRoundGroup(perGroupMatches.get(roundGroupId));
    }

    final List<Integer> playersPerGroup = roundScoreboard.getPlayersPerGroup();
    final String split = MatchUtil.integerListToString(playersPerGroup);
    final List<Integer> xpPoints = xpPointsRepository.retrievePointsBySplit(split);

    roundScoreboard.assignPointsAndPlaces(xpPoints);
    return roundScoreboard;
  }

}
