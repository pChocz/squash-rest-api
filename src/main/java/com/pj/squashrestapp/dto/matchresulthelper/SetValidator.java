package com.pj.squashrestapp.dto.matchresulthelper;

import com.pj.squashrestapp.model.SetResult;
import lombok.AllArgsConstructor;

/**
 *
 */
@AllArgsConstructor
public class SetValidator {

  private static final int REGULAR_SET_WINNING_SCORE = 11;
  private static final int REGULAR_SET_WINNING_ADVANTAGE_SCORE = 12;
  private static final int TIE_BREAK_WINNING_SCORE = 9;

  final private int setNumber;
  final private Integer firstPlayerScore;
  final private Integer secondPlayerScore;

  public SetStatus checkStatus() {

    if (firstPlayerScore == null && secondPlayerScore == null) {
      return SetStatus.EMPTY;

    } else if (firstPlayerScore == null || secondPlayerScore == null) {
      return SetStatus.IN_PROGRESS;

    }

    final int high = Math.max(firstPlayerScore, secondPlayerScore);
    final int low = Math.min(firstPlayerScore, secondPlayerScore);

    if (isValidSet(setNumber, high, low)) {
      return checkWinner();

    } else {
      return SetStatus.ERROR;
    }
  }

  private boolean isValidSet(final int setNumber, final int high, final int low) {
    if (isRegularSet(setNumber) && isValidRegularSet(high, low)) {
      return true;

    } else if (isTiebreak(setNumber) && isValidTieBreak(high, low)) {
      return true;

    } else {
      return false;

    }
  }

  public SetStatus checkWinner() {
    return firstPlayerScore > secondPlayerScore
            ? SetStatus.FIRST_PLAYER_WINS

            : firstPlayerScore < secondPlayerScore
            ? SetStatus.SECOND_PLAYER_WINS

            // will never happen
            : null;
  }

  private boolean isRegularSet(final int setNumber) {
    return setNumber == 1 || setNumber == 2;
  }

  private boolean isValidRegularSet(final int high, final int low) {
    final int delta = high - low;
    if (high == REGULAR_SET_WINNING_SCORE && delta > 1) {
      return true;
    } else if (high == REGULAR_SET_WINNING_ADVANTAGE_SCORE && isInRange(delta, 1, 2)) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isTiebreak(final int setNumber) {
    return setNumber == 3;
  }

  private boolean isValidTieBreak(final int high, final int low) {
    if (high == TIE_BREAK_WINNING_SCORE && (high - low) > 0) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isInRange(final int value, final int min, final int max) {
    return value >= min && value <= max;
  }

}
