package com.pj.squashrestapp.service;

import com.google.common.util.concurrent.AtomicLongMap;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.MatchesGroupedDto;
import com.pj.squashrestapp.model.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.model.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;
  private final SetResultRepository setResultRepository;
  private final PlayerRepository playerRepository;

  public void modifySingleScore(final UUID matchUuid, final int setNumber, final String player, final Integer looserScore) {
    final Match matchToModify = matchRepository.findMatchByUuid(matchUuid).orElseThrow();

    final String initialMatchResult = matchToModify.toString();

    final SetResult setToModify = matchToModify
            .getSetResults()
            .stream()
            .filter(set -> set.getNumber() == setNumber)
            .findFirst()
            .orElse(null);

    if (looserScore == null) {
      setToModify.setFirstPlayerScore(null);
      setToModify.setSecondPlayerScore(null);

    } else {
      final Integer winnerScore = computeWinnerScore(looserScore, setNumber);

      if (player.equals("FIRST")) {
        setToModify.setFirstPlayerScore(looserScore);
        setToModify.setSecondPlayerScore(winnerScore);

      } else if (player.equals("SECOND")) {
        setToModify.setFirstPlayerScore(winnerScore);
        setToModify.setSecondPlayerScore(looserScore);
      }
    }

    setResultRepository.save(setToModify);

    final String message = "\nSuccesfully updated the match!" +
                           "\n\t-> " + initialMatchResult + "\t- earlier" +
                           "\n\t-> " + matchToModify + "\t- now";
    log.info(message);
  }

  private Integer computeWinnerScore(final Integer looserScore, final int setNumber) {
    if (setNumber < 3) {
      return computeWinnerScoreForRegularSet(looserScore);
    } else {
      return 9;
    }
  }

  private Integer computeWinnerScoreForRegularSet(final Integer looserScore) {
    if (looserScore < 10) {
      return 11;
    } else {
      return 12;
    }
  }

  public MatchesSimplePaginated getMatchesPaginated(final Pageable pageable, final UUID leagueUuid, final UUID[] playersUuids) {
    final Page<Long> matchIds = (playersUuids.length == 1)
            ? matchRepository.findIdsSingle(leagueUuid, playersUuids[0], pageable)
            : matchRepository.findIdsMultiple(leagueUuid, playersUuids, pageable);

    final List<Match> matches = matchRepository.findByIdIn(matchIds.getContent());

    final List<MatchSimpleDto> matchesDtos = matches
            .stream()
            .map(MatchSimpleDto::new)
            .collect(Collectors.toList());

    final MatchesSimplePaginated matchesDtoPage = new MatchesSimplePaginated(matchIds, matchesDtos);
    return matchesDtoPage;
  }

  public MatchesSimplePaginated getMatchesPaginatedMeAgainstAll(final Pageable pageable, final UUID leagueUuid, final UUID[] playersUuids) {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final Page<Long> matchIds = matchRepository.findIdsSingleAgainstOthers(leagueUuid, currentPlayerUuid, playersUuids, pageable);
    final List<Match> matches = matchRepository.findByIdIn(matchIds.getContent());

    final List<MatchSimpleDto> matchesDtos = matches
            .stream()
            .map(MatchSimpleDto::new)
            .collect(Collectors.toList());

    final MatchesSimplePaginated matchesDtoPage = new MatchesSimplePaginated(matchIds, matchesDtos);
    return matchesDtoPage;
  }

//  public Set<PerLeagueMatchStats> getMyMatchesCountPerPlayers_v1() {
//    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
//    final Player currentPlayer = playerRepository.findByUuid(currentPlayerUuid);
//
//    final List<Match> allMatches = matchRepository.fetchByOnePlayerAgainstAll(currentPlayerUuid);
//
//    final Set<PerLeagueMatchStats> perLeagueMatchStatsList = new TreeSet<>();
//
//    final Multimap<League, Match> map = LinkedHashMultimap.create();
//    for (final Match match : allMatches) {
//      final League league = match.getRoundGroup().getRound().getSeason().getLeague();
//      map.put(league, match);
//    }
//
//    for (final League league : map.keySet()) {
//      final List<Match> leagueMatches = new ArrayList<>(map.get(league));
//
//      final AtomicLongMap<Player> atomicLongMap = AtomicLongMap.create();
//      for (final Match match : leagueMatches) {
//        atomicLongMap.getAndIncrement(match.getFirstPlayer());
//        atomicLongMap.getAndIncrement(match.getSecondPlayer());
//      }
//      atomicLongMap.remove(currentPlayer);
//
//      final PerLeagueMatchStats perLeagueMatchStats = new PerLeagueMatchStats(league, atomicLongMap);
//
//      perLeagueMatchStatsList.add(perLeagueMatchStats);
//    }
//
//    return perLeagueMatchStatsList;
//  }

  public List<PerLeagueMatchStats> getMyMatchesCountPerPlayers() {
    final UUID currentPlayerUuid = GeneralUtil.extractSessionUserUuid();
    final Player currentPlayer = playerRepository.findByUuid(currentPlayerUuid);

    final Set<MatchesGroupedDto> matchesGroupedDtoSet = matchRepository
            .fetchGroupedMatches(currentPlayerUuid)
            .stream()
            .map(object -> new MatchesGroupedDto(object, currentPlayer))
            .collect(Collectors.toSet());

    final List<PerLeagueMatchStats> perLeagueMatchStatsList = new ArrayList<>();

    final List<League> allLeagues = matchesGroupedDtoSet
            .stream()
            .map(MatchesGroupedDto::getLeague)
            .distinct()
            .collect(Collectors.toList());

    for (final League league : allLeagues) {
      final List<MatchesGroupedDto> collect = matchesGroupedDtoSet
              .stream()
              .filter(matchesGroupedDto -> matchesGroupedDto
                      .getLeague()
                      .equals(league))
              .collect(Collectors.toList());

      final AtomicLongMap<Player> atomicLongMap = AtomicLongMap.create();
      for (final MatchesGroupedDto matchesGroupedDto : collect) {
        atomicLongMap.getAndAdd(matchesGroupedDto.getOpponent(), matchesGroupedDto.getCount());
      }

      final PerLeagueMatchStats perLeagueMatchStats = new PerLeagueMatchStats(league, atomicLongMap);
      perLeagueMatchStatsList.add(perLeagueMatchStats);
    }

    perLeagueMatchStatsList.sort(Comparator.comparingLong(PerLeagueMatchStats::getMatches).reversed());
    return perLeagueMatchStatsList;
  }

}
