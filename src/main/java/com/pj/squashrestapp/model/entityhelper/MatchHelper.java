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

}
