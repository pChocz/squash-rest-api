package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.SetResult;
import lombok.experimental.UtilityClass;

/**
 *
 */
@UtilityClass
public class FakeMatch {

  public Match create(final Player firstPlayer, final Player secondPlayer) {
    final Match match = new Match(firstPlayer, secondPlayer);

    match.addSetResult(FakeSetResult.create(1));
    match.addSetResult(FakeSetResult.create(2));

    if (isDrawAfterTwoSets(match)) {
      match.addSetResult(FakeSetResult.create(3));
    } else {
      match.addSetResult(FakeSetResult.createEmpty(3));
    }

    return match;
  }

  private boolean isDrawAfterTwoSets(final Match match) {
    int diff = 0;
    for (final SetResult setResult : match.getSetResults()) {
      if (setResult.getFirstPlayerScore() > setResult.getSecondPlayerScore()) {
        diff++;
      } else {
        diff--;
      }
    }
    return diff == 0;
  }

}
