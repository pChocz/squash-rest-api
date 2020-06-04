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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/round")
public class RoundController {

  @Autowired
  private SeasonRepository seasonRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoundRepository roundRepository;

  
  /**
   * EXAMPLE:
   *  addRoundToSeason ? roundNumber=9 & seasonNumber=5 & leagueId=1 & playersIds=1,2,3 & playersIds=4,5,6
   *
   */
  @RequestMapping(
          value = "/add",
          params = {"roundNumber", "roundDate", "seasonNumber", "leagueId", "playersIds"},
          method = POST)
  @ResponseBody
  @PreAuthorize("hasRoleForLeague(#id, 'ADMIN')")
  String add(
          @RequestParam("roundNumber") final int roundNumber,
          @RequestParam("roundDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate roundDate,
          @RequestParam("seasonNumber") final int seasonNumber,
          @RequestParam("leagueId") final Long leagueId,
          @RequestParam("playersIds") final List<Long[]> playersIds) {

    final Map<Integer, List<Player>> map = new LinkedHashMap<>();
    for (int i=0; i < playersIds.size(); i++) {
      final int groupNumber = i+1;
      final Long[] groupPlayersIds = playersIds.get(i);
      final List<Player> groupPlayers = playerRepository.findByIds(groupPlayersIds);
      map.put(groupNumber, groupPlayers);
    }
    final Season season = seasonRepository.findSeasonByNumberAndLeagueId(seasonNumber, leagueId);

    final Round round = new Round(roundNumber, Date.valueOf(roundDate), season, map);
    roundRepository.save(round);

    return "new Round Id = " + round.getId();
  }


  @RequestMapping(
          value = "/delete",
          params = {"roundId"},
          method = DELETE)
  @ResponseBody
  String delete(
          @RequestParam("roundId") final Long roundId) {

    final Round roundToDelete = roundRepository.findRoundById(roundId);
    roundRepository.delete(roundToDelete);
    return "Round " + roundId + " deleted";
  }


}
