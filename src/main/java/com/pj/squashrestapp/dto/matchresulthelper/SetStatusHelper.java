package com.pj.squashrestapp.dto.matchresulthelper;

import com.pj.squashrestapp.dto.match.SetDto;
import com.pj.squashrestapp.model.SetWinningType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/** */
@Slf4j
@UtilityClass
public class SetStatusHelper {

  public static SetStatus checkStatus(
      final SetDto setResult, final int winningPoints, final SetWinningType winningType) {

    final Integer firstPlayerScore = setResult.getFirstPlayerScore();
    final Integer secondPlayerScore = setResult.getSecondPlayerScore();

    if (firstPlayerScore == null && secondPlayerScore == null) {
      return SetStatus.EMPTY;
    } else if (firstPlayerScore == null || secondPlayerScore == null) {
      return SetStatus.IN_PROGRESS;
    }

    // at this point it means both scores are not null
    final int looserScore = Math.min(firstPlayerScore, secondPlayerScore);
    final int winnerScore = Math.max(firstPlayerScore, secondPlayerScore);

    final int expectedWinnerScore;
    try {
      expectedWinnerScore =
          SetScoreHelper.computeWinnerScore(looserScore, winningPoints, winningType);
    } catch (final WrongResultException e) {
      return SetStatus.ERROR;
    }

    if (winnerScore < expectedWinnerScore) {
      return SetStatus.IN_PROGRESS;
    }

    if (winnerScore != expectedWinnerScore) {
      return SetStatus.ERROR;
    }

    // at this point it should be a proper finished set
    if (firstPlayerScore == winnerScore) {
      return SetStatus.FIRST_PLAYER_WINS;
    } else if (secondPlayerScore == winnerScore) {
      return SetStatus.SECOND_PLAYER_WINS;
    }

    // this point should never be reached
    log.error(
        "Strange set result, it means probably that it was manually modified somehow! {} : {}",
        firstPlayerScore,
        secondPlayerScore);
    return SetStatus.ERROR;
  }
}
