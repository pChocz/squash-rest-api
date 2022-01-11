package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoundGroupRepository;
import com.pj.squashrestapp.repository.RoundRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoundService {

  private final SeasonRepository seasonRepository;
  private final PlayerRepository playerRepository;
  private final RoundRepository roundRepository;
  private final RoundGroupRepository roundGroupRepository;
  private final RedisCacheService redisCacheService;

  public void deleteRound(final UUID roundUuid) {
    final Round roundToDelete = roundRepository.findByUuidWithSeason(roundUuid);
    roundRepository.delete(roundToDelete);
    redisCacheService.evictCacheForRound(roundToDelete);
  }

  @Transactional
  public Round createRound(
      final int roundNumber,
      final LocalDate roundDate,
      final UUID seasonUuid,
      final List<UUID[]> playersUuids) {

    final List<List<Player>> playersPerGroup = getPlayersPerGroups(playersUuids);

    final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
    final League league = season.getLeague();
    final int setsPerMatch = league.getMatchFormatType().getMaxNumberOfSets();

    final Round round =
        createRoundForSeasonWithGivenPlayers(
            league, roundNumber, roundDate, playersPerGroup, setsPerMatch);
    season.addRound(round);

    // saving to DB
    roundRepository.save(round);

    redisCacheService.evictCacheForRound(round);

    return round;
  }

  private List<List<Player>> getPlayersPerGroups(final List<UUID[]> playersUuids) {
    final UUID[] allPlayersUuids = playersUuids.stream().flatMap(Arrays::stream).toArray(UUID[]::new);

    final List<Player> allPlayers = playerRepository.findByUuids(allPlayersUuids);

    final List<Player> allPlayersOrdered =
        Arrays.stream(allPlayersUuids)
            .map(
                uuid ->
                    allPlayers.stream()
                        .filter(p -> p.getUuid().equals(uuid))
                        .findFirst()
                        .orElse(null))
            .collect(Collectors.toList());

    final List<List<Player>> playersPerGroup =
        playersUuids.stream()
            .filter(uuids -> uuids.length > 0)
            .map(uuid -> Arrays.stream(uuid).collect(Collectors.toList()))
            .map(
                uuidsForCurrentGroup ->
                    allPlayersOrdered.stream()
                        .filter(player -> uuidsForCurrentGroup.contains(player.getUuid()))
                        .collect(Collectors.toList()))
            .collect(Collectors.toList());
    return playersPerGroup;
  }

  private Round createRoundForSeasonWithGivenPlayers(
      final League league,
      final int roundNumber,
      final LocalDate roundDate,
      final List<List<Player>> playersPerGroup,
      final int setsPerMatch) {

    final Round round = new Round();
    round.setNumber(roundNumber);
    round.setDate(roundDate);

    final List<Integer> countPerRound =
        playersPerGroup.stream().map(List::size).collect(Collectors.toList());

    round.setSplit(GeneralUtil.integerListToString(countPerRound));

    for (int i = 1; i <= playersPerGroup.size(); i++) {
      final RoundGroup roundGroup = createRoundGroup(league, playersPerGroup, i, setsPerMatch);
      round.addRoundGroup(roundGroup);
    }

    return round;
  }

  private RoundGroup createRoundGroup(
      final League league,
      final List<List<Player>> playersPerGroup,
      final int groupNumber,
      final int setsPerMatch) {

    final RoundGroup roundGroup = new RoundGroup();
    roundGroup.setNumber(groupNumber);

    int matchNumber = 1;
    final List<Player> groupPlayers = playersPerGroup.get(groupNumber - 1);
    for (int j = 0; j < groupPlayers.size(); j++) {
      for (int k = j + 1; k < groupPlayers.size(); k++) {
        final Player firstPlayer = groupPlayers.get(j);
        final Player secondPlayer = groupPlayers.get(k);
        final Match match =
            createMatch(matchNumber++, firstPlayer, secondPlayer, setsPerMatch, league);
        roundGroup.addMatch(match);
      }
    }
    return roundGroup;
  }

  private Match createMatch(
      final int number,
      final Player firstPlayer,
      final Player secondPlayer,
      final int setsPerMatch,
      final League league) {

    final Match match = new Match(firstPlayer, secondPlayer, league);
    match.setNumber(number);

    for (int i = 1; i <= setsPerMatch; i++) {
      final SetResult setResult = new SetResult();
      setResult.setNumber(i);
      setResult.setFirstPlayerScore(null);
      setResult.setSecondPlayerScore(null);

      match.addSetResult(setResult);
    }
    return match;
  }

  public void updateRoundFinishedState(final UUID roundUuid, final boolean finishedState) {
    final Round round = roundRepository.findByUuidWithSeason(roundUuid);
    round.setFinished(finishedState);
    redisCacheService.evictCacheForRound(round);
    roundRepository.save(round);
  }

  public UUID extractLeagueUuid(final UUID roundUuid) {
    return roundRepository.retrieveLeagueUuidOfRound(roundUuid);
  }

  @Transactional
  public Round recreateRound(final UUID roundUuid, final List<UUID[]> playersUuids) {
    final Round round = roundRepository.findByUuidWithSeasonLeague(roundUuid);
    final League league = round.getSeason().getLeague();
    final int setsPerMatch = league.getMatchFormatType().getMaxNumberOfSets();

    final List<List<Player>> playersPerGroup = getPlayersPerGroups(playersUuids);

    final List<Integer> countPerRound =
        playersPerGroup.stream().map(List::size).collect(Collectors.toList());

    round.setSplit(GeneralUtil.integerListToString(countPerRound));

    // deleting old round groups
    final Iterator<RoundGroup> iterator = round.getRoundGroups().iterator();
    while (iterator.hasNext()) {
      final RoundGroup roundGroup = iterator.next();
      iterator.remove();
      roundGroupRepository.delete(roundGroup);
    }

    roundGroupRepository.flush();

    // creating new round groups
    for (int i = 1; i <= playersPerGroup.size(); i++) {
      final RoundGroup roundGroup = createRoundGroup(league, playersPerGroup, i, setsPerMatch);
      round.addRoundGroup(roundGroup);
      roundGroupRepository.save(roundGroup);
    }

    redisCacheService.evictCacheForRound(round);

    return round;
  }

}
