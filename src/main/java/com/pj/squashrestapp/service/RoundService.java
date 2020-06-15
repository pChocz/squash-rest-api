package com.pj.squashrestapp.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 */
@Service
public class RoundService {

  @Autowired
  private SeasonRepository seasonRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoundRepository roundRepository;

  public void deleteRound(final Long roundId) {
    final Round roundToDelete = roundRepository.findRoundById(roundId);
    roundRepository.delete(roundToDelete);
  }

  public Round createRound(final int roundNumber, final LocalDate roundDate, final int seasonNumber, final Long leagueId, final List<Long[]> playersIds) {
    final Long[] allPlayersIds = playersIds.stream().flatMap(Arrays::stream).toArray(Long[]::new);

    // repos queries from DB
    final List<Player> allPlayers = playerRepository.findByIds(allPlayersIds);
    final Season season = seasonRepository.findSeasonByNumberAndLeagueId(seasonNumber, leagueId);

    final List<List<Player>> playersPerGroup = playersIds
            .stream()
            .map(playersId -> Arrays
                    .stream(playersId)
                    .collect(Collectors.toList()))
            .map(idsForCurrentGroup -> allPlayers
                    .stream()
                    .filter(player -> idsForCurrentGroup.contains(player.getId()))
                    .collect(Collectors.toList()))
            .collect(Collectors.toList());

    final Round round = createRoundForSeasonWithGivenPlayers(roundNumber, roundDate, playersPerGroup);
    season.addRound(round);

    // saving to DB
    roundRepository.save(round);

    return round;
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
      final RoundGroup roundGroup = createRoundGroup(playersPerGroup, i);
      round.addRoundGroup(roundGroup);
    }

    return round;
  }

  private RoundGroup createRoundGroup(final List<List<Player>> playersPerGroup, final int i) {
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
    return roundGroup;
  }

}
