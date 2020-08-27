package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dbinit.jsondto.JsonRoundGroup;
import com.pj.squashrestapp.dbinit.jsondto.JsonMatch;
import com.pj.squashrestapp.dbinit.jsondto.JsonPlayer;
import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.dbinit.jsondto.JsonSetResult;
import com.pj.squashrestapp.dbinit.jsondto.util.JsonExportUtil;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class RoundService {

  @Autowired
  private SetResultRepository setResultRepository;

  @Autowired
  private SeasonRepository seasonRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private RoundRepository roundRepository;

  public void deleteRound(final UUID roundUuid) {
    final Round roundToDelete = roundRepository.findRoundByUuid(roundUuid);
    roundRepository.delete(roundToDelete);
  }

  @Transactional
  public Round createRound(final int roundNumber, final LocalDate roundDate, final UUID seasonUuid, final List<Long[]> playersIds) {
    final Long[] allPlayersIds = playersIds.stream().flatMap(Arrays::stream).toArray(Long[]::new);

    // repos queries from DB
    final List<Player> allPlayersOrderedById = playerRepository.findByIds(allPlayersIds);

    final List<Player> allPlayersOrderedProperly = Arrays
            .stream(allPlayersIds)
            .map(id -> allPlayersOrderedById
                    .stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null))
            .collect(Collectors.toList());

    final Season season = seasonRepository.findSeasonByUuid(seasonUuid);

    final List<List<Player>> playersPerGroup = playersIds
            .stream()
            .map(playersId -> Arrays
                    .stream(playersId)
                    .collect(Collectors.toList()))
            .map(idsForCurrentGroup -> allPlayersOrderedProperly
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

  // this one will be deleted later
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

  public String roundToJson(final UUID roundUuid) {
    final List<SetResult> setResults = setResultRepository.fetchByRoundId(roundUuid);
    final Long roundId = roundRepository.findIdByUuid(roundUuid);
    final Round round = EntityGraphBuildUtil.reconstructRound(setResults, roundId);
    final String roundJson = JsonExportUtil.backupRoundToJson(round);
    return roundJson;
  }

}
