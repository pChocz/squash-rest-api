package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoundGroup;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 *
 */
@UtilityClass
public class FakeRoundGroup {

  public RoundGroup create(final int roundGroupNumber, final List<Player> players) {
    final RoundGroup roundGroup = new RoundGroup(roundGroupNumber);
    for (int i = 0; i < players.size(); i++) {
      for (int j = i + 1; j < players.size(); j++) {
        final Player firstPlayer = players.get(i);
        final Player secondPlayer = players.get(j);
        final Match fakeMatch = FakeMatch.create(firstPlayer, secondPlayer);
        roundGroup.addMatch(fakeMatch);
      }
    }
    return roundGroup;
  }

}
