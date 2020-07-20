package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.SetResult;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
@UtilityClass
public class FakeSetResult {

  private static final int WINNING_REGULAR_SET_SCORE = 11;
  private static final int WINNING_REGULAR_SET_SCORE_ADVANTAGE = 12;
  private static final int WINNING_TIE_BREAK_SCORE = 9;

  public SetResult create(final int setNumber) {
    final int winningSetScore = generateWinningPoints(setNumber);
    final int[] result = createFakeSetResultArray(winningSetScore);
    return new SetResult(setNumber, result[0], result[1]);
  }

  public SetResult createEmpty(final int setNumber) {
    return new SetResult(setNumber, 0, 0);
  }

  private int[] createFakeSetResultArray(final int winningPoints) {
    final int[] result = new int[2];

    final boolean firstPlayerWon = randBetween(0, 1) == 1 ? true : false;
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

  private int randBetween(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  private int generateWinningPoints(final int setNumber) {
    return setNumber > 2
            ? WINNING_TIE_BREAK_SCORE
            : WINNING_REGULAR_SET_SCORE;
  }

  private int generateLoosingPoints(final int winningPoints) {
    return winningPoints == WINNING_REGULAR_SET_SCORE
            ? randBetween(0, winningPoints)
            : randBetween(0, WINNING_TIE_BREAK_SCORE - 1);
  }

}
