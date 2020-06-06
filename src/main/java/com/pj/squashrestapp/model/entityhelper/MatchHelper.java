package com.pj.squashrestapp.model.entityhelper;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.SetResult;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchHelper {

  final private Match match;

  public Player getWinner() {
    final int[] result = new int[2];

    for (final SetResult setResult : match.getSetResults()) {
      if (setResult.getFirstPlayerScore() > setResult.getSecondPlayerScore()) {
        result[0]++;
      } else if (setResult.getFirstPlayerScore() < setResult.getSecondPlayerScore()) {
        result[1]++;
      }
    }

    if (result[0] == result[1] || result[0] + result[1] < 2) {
      return null;

    } else if (result[0] > result[1]) {
      return match.getFirstPlayer();

    } else if (result[0] < result[1]) {
      return match.getSecondPlayer();
    }

    // should never happen
    return null;
  }

  public boolean isValid() {
    final SetResult setResult1 = getSetResult(1);
    final SetResult setResult2 = getSetResult(2);
    final SetResult setResult3 = getSetResult(3);

    if (isEmptySet(setResult1)
            && !isEmptySet(setResult2)) {
      return false;

    } else if (isEmptySet(setResult1)
            && !isEmptySet(setResult3)) {
      return false;

    } else if (isEmptySet(setResult2)
            && !isEmptySet(setResult3)) {
      return false;

    } else if (isFinishedAfterTwoSets(setResult1, setResult2)
            && !isEmptySet(setResult3)) {
      return false;

    } else {
      return true;
    }
  }

  private SetResult getSetResult(final int number) {
    return match
            .getSetResults()
            .stream()
            .filter(set -> set.getNumber() == number)
            .findFirst()
            .orElse(null);
  }

  private boolean isEmptySet(final SetResult setResult) {
    return setResult.getFirstPlayerScore() == 0
            && setResult.getSecondPlayerScore() == 0;
  }

  private boolean isFinishedAfterTwoSets(final SetResult setResult1, final SetResult setResult2) {
    final Player winner1 = setResult1.getWinner();
    final Player winner2 = setResult2.getWinner();
    return winner1.equals(winner2);
  }

}
