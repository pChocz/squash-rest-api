package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.SetWinningType;
import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class FakeSetResult {

  AdditionalSetResult createAdditional(final int setNumber, final SetWinningType winningType, final int expectedWinningPoints) {
    final int[] result = createFakeSetResultArray(winningType, expectedWinningPoints);
    return new AdditionalSetResult(setNumber, result[0], result[1]);
  }

  private int[] createFakeSetResultArray(final SetWinningType winningType, final int expectedWinningPoints) {
    final int realWinningPoints = switch (winningType) {
      case ADV_OF_2_ABSOLUTE -> {
        final int random = FakeUtil.randomBetweenTwoIntegers(0, 20);
        yield random < 12
            ? expectedWinningPoints
            : random < 15
                ? expectedWinningPoints + 1
                : random < 17
                    ? expectedWinningPoints + 2
                    : expectedWinningPoints + 3;

      }
      case WINNING_POINTS_ABSOLUTE -> {
        yield expectedWinningPoints;

      }
      case ADV_OF_2_OR_1_AT_THE_END -> {
        yield FakeUtil.randomBetweenTwoIntegers(0, 10) < 7
            ? expectedWinningPoints
            : expectedWinningPoints + 1;
      }
    };

    final int realLoosingPoints = generateLoosingPoints(winningType, expectedWinningPoints, realWinningPoints);

    return FakeUtil.randomBetweenTwoIntegers(0, 1) == 1
        ? new int[] {realWinningPoints, realLoosingPoints}
        : new int[] {realLoosingPoints, realWinningPoints};
  }

  private int generateLoosingPoints(final SetWinningType winningType, final int expectedWinningPoints, final int realWinningPoints) {
    switch (winningType) {
      case ADV_OF_2_OR_1_AT_THE_END -> {
        if (expectedWinningPoints == realWinningPoints) {
          return FakeUtil.randomBetweenTwoIntegers(0, expectedWinningPoints - 2);
        } else {
          return FakeUtil.randomBetweenTwoIntegers(expectedWinningPoints - 1, expectedWinningPoints);
        }
      }
      case WINNING_POINTS_ABSOLUTE -> {
        return FakeUtil.randomBetweenTwoIntegers(0, realWinningPoints - 1);
      }
      case ADV_OF_2_ABSOLUTE -> {
        if (expectedWinningPoints == realWinningPoints) {
          return FakeUtil.randomBetweenTwoIntegers(0, expectedWinningPoints - 2);
        } else {
          return realWinningPoints - 2;
        }
      }
    }
    throw new RuntimeException("Unsupported SetWinningType");
  }

  SetResult create(final int setNumber, final SetWinningType winningType, final int expectedWinningPoints) {
    final int[] result = createFakeSetResultArray(winningType, expectedWinningPoints);
    return new SetResult(setNumber, result[0], result[1]);
  }

  SetResult createNullSet(final int setNumber) {
    return new SetResult(setNumber, null, null);
  }

  AdditionalSetResult createNullSetAdditional(final int setNumber) {
    return new AdditionalSetResult(setNumber, null, null);
  }
}
