package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.util.GeneralUtil;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
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
  @PreAuthorize("hasRoleForLeague(#leagueId, 'MODERATOR')")
  String add(
          @RequestParam("roundNumber") final int roundNumber,
          @RequestParam("roundDate") @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate roundDate,
          @RequestParam("seasonNumber") final int seasonNumber,
          @RequestParam("leagueId") final Long leagueId,
          @RequestParam("playersIds") final List<Long[]> playersIds) {

    final Long[] allPlayersIds = playersIds.stream().flatMap(Arrays::stream).toArray(Long[]::new);

    // repos queries from DB
    final Season season = seasonRepository.findSeasonByNumberAndLeagueId(seasonNumber, leagueId);
    final List<Player> allPlayers = playerRepository.findByIds(allPlayersIds);

    final List<List<Player>> playersPerGroup = new ArrayList<>();
    for (int i = 0; i < playersIds.size(); i++) {
      final List<Long> idsForCurrentGroup = Arrays
              .stream(playersIds.get(i))
              .collect(Collectors.toList());
      final List<Player> playersForCurrentGroup = allPlayers
              .stream()
              .filter(player -> idsForCurrentGroup.contains(player.getId()))
              .collect(Collectors.toList());
      playersPerGroup.add(playersForCurrentGroup);
    }

    final Round round = createRoundForSeasonWithGivenPlayers(roundNumber, roundDate, playersPerGroup);
    season.addRound(round);

    // saving to DB
    roundRepository.save(round);
    return "new Round Id = " + round.getId();
  }

  private Round createRoundForSeasonWithGivenPlayers(
          final int roundNumber,
          final LocalDate roundDate,
          final List<List<Player>> playersPerGroup) {

    final Round round = new Round();
    round.setNumber(roundNumber);
    round.setDate(Date.valueOf(roundDate));
    final List<Integer> countPerRound = playersPerGroup
            .stream()
            .map(List::size)
            .collect(Collectors.toList());
    round.setSplit(GeneralUtil.integerListToString(countPerRound));

    for (int i = 0; i < playersPerGroup.size(); i++) {
      final RoundGroup roundGroup = new RoundGroup();
      final int groupNumber = i + 1;
      roundGroup.setNumber(groupNumber);

      final List<Player> groupPlayers = playersPerGroup.get(0);
      for (int j = 0; j < groupPlayers.size(); j++) {
        for (int k = j + 1; k < groupPlayers.size(); k++) {
          final Match match = new Match();
          match.setFirstPlayer(groupPlayers.get(j));
          match.setSecondPlayer(groupPlayers.get(k));

          for (int l = 0; l < 3; l++) {
            final SetResult setResult = new SetResult();
            setResult.setNumber(l + 1);
            setResult.setFirstPlayerScore(0);
            setResult.setSecondPlayerScore(0);

            match.addSetResult(setResult);
          }
          roundGroup.addMatch(match);
        }
      }
      round.addRoundGroup(roundGroup);
    }

    return round;
  }

  @RequestMapping(
          value = "/delete",
          params = {"roundId"},
          method = DELETE)
  @ResponseBody
  @PreAuthorize("hasRoleForRound(#roundId, 'MODERATOR')")
  String delete(
          @RequestParam("roundId") final Long roundId) {

    final Round roundToDelete = roundRepository.findRoundById(roundId);
    roundRepository.delete(roundToDelete);
    return "Round " + roundId + " deleted";
  }


}
