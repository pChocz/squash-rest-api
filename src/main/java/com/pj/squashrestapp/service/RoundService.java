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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoundService {

  private final SeasonRepository seasonRepository;
  private final PlayerRepository playerRepository;
  private final RoundRepository roundRepository;


  public void deleteRound(final UUID roundUuid) {
    final Round roundToDelete = roundRepository.findByUuid(roundUuid).orElseThrow();
    roundRepository.delete(roundToDelete);
  }

  @Transactional
  public Round createRound(final int roundNumber, final LocalDate roundDate, final UUID seasonUuid, final List<UUID[]> playersUuids) {
    final UUID[] allPlayersUuids = playersUuids.stream().flatMap(Arrays::stream).toArray(UUID[]::new);

    final List<Player> allPlayers = playerRepository.findByUuids(allPlayersUuids);

    final List<Player> allPlayersOrdered = Arrays
            .stream(allPlayersUuids)
            .map(uuid -> allPlayers
                    .stream()
                    .filter(p -> p.getUuid().equals(uuid))
                    .findFirst()
                    .orElse(null))
            .collect(Collectors.toList());

    final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();

    final List<List<Player>> playersPerGroup = playersUuids
            .stream()
            .filter(uuids -> uuids.length > 0)
            .map(uuid -> Arrays
                    .stream(uuid)
                    .collect(Collectors.toList()))
            .map(uuidsForCurrentGroup -> allPlayersOrdered
                    .stream()
                    .filter(player -> uuidsForCurrentGroup.contains(player.getUuid()))
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
    round.setDate(roundDate);

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

    int matchNumber = 1;

    final List<Player> groupPlayers = playersPerGroup.get(i);
    for (int j = 0; j < groupPlayers.size(); j++) {
      for (int k = j + 1; k < groupPlayers.size(); k++) {
        final Match match = new Match();
        match.setFirstPlayer(groupPlayers.get(j));
        match.setSecondPlayer(groupPlayers.get(k));
        match.setNumber(matchNumber++);

        for (int l = 0; l < 3; l++) {
          final SetResult setResult = new SetResult();
          setResult.setNumber(l + 1);
          setResult.setFirstPlayerScore(null);
          setResult.setSecondPlayerScore(null);

          match.addSetResult(setResult);
        }
        roundGroup.addMatch(match);
      }
    }
    return roundGroup;
  }

  public void updateRoundFinishedState(final UUID roundUuid, final boolean finishedState) {
    final Round round = roundRepository.findByUuid(roundUuid).orElseThrow();
    round.setFinished(finishedState);
    roundRepository.save(round);
  }

  public UUID extractLeagueUuid(final UUID roundUuid) {
    return roundRepository.retrieveLeagueUuidOfRound(roundUuid);
  }

}
