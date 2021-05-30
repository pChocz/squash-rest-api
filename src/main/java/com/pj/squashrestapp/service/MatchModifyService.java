package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.repository.AdditionalMatchRepository;
import com.pj.squashrestapp.repository.AdditionalSetResultRepository;
import com.pj.squashrestapp.repository.MatchRepository;
import com.pj.squashrestapp.repository.SetResultRepository;
import com.pj.squashrestapp.dto.matchresulthelper.SetScoreHelper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

/** Common service for modifications of both additional and round matches. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchModifyService {

  private final MatchRepository matchRepository;
  private final SetResultRepository setResultRepository;
  private final AdditionalMatchRepository additionalMatchRepository;
  private final AdditionalSetResultRepository additonalSetResultRepository;

  public void modifySingleScoreForAdditionalMatch(
      final UUID matchUuid, final int setNumber, final String player, final Integer looserScore) {

    final AdditionalMatch match = additionalMatchRepository.findByUuid(matchUuid).orElseThrow();
    final League league = match.getLeague();
    final AdditionalSetResult setToModify =
        match.getSetResults().stream()
            .filter(set -> set.getNumber() == setNumber)
            .findFirst()
            .orElse(null);
    final int numberOfSets = match.getNumberOfSets();
    final boolean isTiebreak = numberOfSets == setNumber;

    final String initialMatchResult = match.toString();

    if (looserScore == -1) {
      setToModify.setFirstPlayerScore(null);
      setToModify.setSecondPlayerScore(null);
    }

    final Pair<Integer, Integer> scores = buildScores(looserScore, player, isTiebreak, league);
    setToModify.setFirstPlayerScore(scores.getFirst());
    setToModify.setSecondPlayerScore(scores.getSecond());

    additonalSetResultRepository.save(setToModify);

    log.info(
        "Succesfully updated additional match!\n\t-> {}\t- earlier\n\t-> {}\t- now",
        initialMatchResult,
        match);
  }

  private Pair<Integer, Integer> buildScores(
      final Integer looserScore,
      final String player,
      final boolean isTiebreak,
      final League league) {

    final Integer firstPlayerScore;
    final Integer secondPlayerScore;

    if (looserScore == -1) {
      firstPlayerScore = null;
      secondPlayerScore = null;

    } else {
      final Integer winnerScore = computeWinnerScoreForLeague(looserScore, isTiebreak, league);
      firstPlayerScore = player.equals("FIRST") ? looserScore : winnerScore;
      secondPlayerScore = player.equals("FIRST") ? winnerScore : looserScore;
    }

    return Pair.of(firstPlayerScore, secondPlayerScore);
  }

  public void modifySingleScoreForRoundMatch(
      final UUID matchUuid, final int setNumber, final String player, final Integer looserScore) {

    final Match match = matchRepository.findMatchByUuid(matchUuid).orElseThrow();
    final String initialMatchResult = match.toString();
    final SetResult setToModify =
        match.getSetResults().stream()
            .filter(set -> set.getNumber() == setNumber)
            .findFirst()
            .orElse(null);

    final Integer firstPlayerScore;
    final Integer secondPlayerScore;
    if (looserScore == -1) {
      firstPlayerScore = null;
      secondPlayerScore = null;

    } else {
      final League league = match.getRoundGroup().getRound().getSeason().getLeague();
      final int numberOfSets = match.getNumberOfSets();
      final boolean isTiebreak = numberOfSets == setNumber;
      final Integer winnerScore = computeWinnerScoreForLeague(looserScore, isTiebreak, league);
      firstPlayerScore = player.equals("FIRST") ? looserScore : winnerScore;
      secondPlayerScore = player.equals("FIRST") ? winnerScore : looserScore;
    }

    setToModify.setFirstPlayerScore(firstPlayerScore);
    setToModify.setSecondPlayerScore(secondPlayerScore);
    setResultRepository.save(setToModify);

    log.info(
        "Succesfully updated round match!\n\t-> {}\t- earlier\n\t-> {}\t- now",
        initialMatchResult,
        match);
  }


  private Integer computeWinnerScoreForLeague(
      final Integer looserScore,
      final boolean isTiebreak,
      final League league) {

//    final int setWinningPoints =
//        isTiebreak ? league.getTiebreakWinningPoints() : league.getRegularSetWinningPoints();
//    final SetWinningType setWinningType =
//        isTiebreak ? league.getTiebreakWinningType() : league.getRegularSetWinningType();

    final int setWinningPoints =
        isTiebreak ? 9 : 11;

    final SetWinningType setWinningType =
        isTiebreak ? SetWinningType.WINNING_POINTS_ABSOLUTE : SetWinningType.ADV_OF_2_OR_1_AT_THE_END;

    final Integer winnerScore = SetScoreHelper.computeWinnerScore(looserScore, setWinningPoints, setWinningType);

    return winnerScore;
  }




}
