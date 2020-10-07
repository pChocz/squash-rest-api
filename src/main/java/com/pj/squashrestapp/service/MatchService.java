package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.dto.match.MatchSimpleDto;
import com.pj.squashrestapp.model.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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

}
