package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.AdditonalSetResult;
import com.pj.squashrestapp.model.SetResult;
import lombok.experimental.UtilityClass;

/**
 *
 */
@UtilityClass
public class FakeSetResult {

  private final int WINNING_REGULAR_SET_SCORE = 11;
  private final int WINNING_REGULAR_SET_SCORE_ADVANTAGE = 12;
  private final int WINNING_TIE_BREAK_SCORE = 9;

  AdditonalSetResult createAdditional(final int setNumber) {
    final int winningSetScore = generateWinningPoints(setNumber);
    final int[] result = createFakeSetResultArray(winningSetScore);
    return new AdditonalSetResult(setNumber, result[0], result[1]);
  }

  SetResult create(final int setNumber) {
    final int winningSetScore = generateWinningPoints(setNumber);
    final int[] result = createFakeSetResultArray(winningSetScore);
    return new SetResult(setNumber, result[0], result[1]);
  }

  private int generateWinningPoints(final int setNumber) {
    return setNumber > 2
            ? WINNING_TIE_BREAK_SCORE
            : WINNING_REGULAR_SET_SCORE;
  }

  private int[] createFakeSetResultArray(final int winningPoints) {
    final int[] result = new int[2];

    final boolean firstPlayerWon = FakeUtil.randomBetweenTwoIntegers(0, 1) == 1 ? true : false;
    if (firstPlayerWon) {
      result[0] = winningPoints;
      result[1] = generateLoosingPoints(winningPoints);
      if (result[1] > 9) {
        result[0] = WINNING_REGULAR_SET_SCORE_ADVANTAGE;
      }
    } else {
      result[1] = winningPoints;
      result[0] = generateLoosingPoints(winningPoints);
      if (result[0] > 9) {
        result[1] = WINNING_REGULAR_SET_SCORE_ADVANTAGE;
      }
    }
    return result;
  }

  private int generateLoosingPoints(final int winningPoints) {
    return winningPoints == WINNING_REGULAR_SET_SCORE
            ? FakeUtil.randomBetweenTwoIntegers(0, winningPoints)
            : FakeUtil.randomBetweenTwoIntegers(0, WINNING_TIE_BREAK_SCORE - 1);
  }

  SetResult createNullSet(final int setNumber) {
    return new SetResult(setNumber, null, null);
  }

  AdditonalSetResult createNullSetAdditional(final int setNumber) {
    return new AdditonalSetResult(setNumber, null, null);
  }

}
