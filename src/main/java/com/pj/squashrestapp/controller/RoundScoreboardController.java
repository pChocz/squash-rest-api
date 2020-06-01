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

  @Autowired
  private SeasonRepository seasonRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoundRepository roundRepository;

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


  /**
   * EXAMPLE:
   *  addRoundToSeason ? roundNumber=7 & seasonId=2 & playersIds=1,2,3 & playersIds=4,5,6
   *
   */
  @RequestMapping(
          value = "/addRoundToSeason",
          params = {"roundNumber", "roundDate", "seasonId", "playersIds"},
          method = POST)
  @ResponseBody
  String addRoundToSeason(
          @RequestParam("roundNumber") final int roundNumber,
          @RequestParam("roundDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate roundDate,
          @RequestParam("seasonId") final Long seasonId,
          @RequestParam("playersIds") final List<Long[]> playersIds) {

    final Map<Integer, List<Player>> map = new LinkedHashMap<>();
    for (int i=0; i < playersIds.size(); i++) {
      final int groupNumber = i+1;
      final Long[] groupPlayersIds = playersIds.get(i);
      final List<Player> groupPlayers = playerRepository.findByIds(groupPlayersIds);
      map.put(groupNumber, groupPlayers);
    }
    final Season season = seasonRepository.findSeasonById(seasonId);

    final Round round = new Round(roundNumber, Date.valueOf(roundDate), season, map);
    roundRepository.save(round);
    return "dupa";
  }


}
