package com.pj.squashrestapp.model.entityhelper;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.SetResult;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SetResultHelper {

  private static final int REGULAR_SET_WINNING_SCORE = 11;
  private static final int REGULAR_SET_WINNING_ADVANTAGE_SCORE = 12;
  private static final int TIE_BREAK_WINNING_SCORE = 9;

  final private SetResult setResult;

  public boolean isValid() {
    final int setNumber = setResult.getNumber();
    final int firstScore = setResult.getFirstPlayerScore();
    final int secondScore = setResult.getSecondPlayerScore();
    final int high = Math.max(firstScore, secondScore);
    final int low = Math.min(firstScore, secondScore);
    if (high == low || high < 0 || low < 0) {
      return false;
    }

    if (setNumber == 1 || setNumber == 2) {
      return isValidRegularSet(high, low);

    } else if (setNumber == 3) {
      return isValidTieBreak(high, low);
    }

    // should never happen
    return false;
  }

  private boolean isValidRegularSet(final int high, final int low) {
    final int delta = high - low;
    if (high == REGULAR_SET_WINNING_SCORE && delta > 1 ) {
      return true;
    } else if (high == REGULAR_SET_WINNING_ADVANTAGE_SCORE && isInRange(delta, 1, 2)) {
      return true;
    } else {
      return false;
    }
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
