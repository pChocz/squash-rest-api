package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import lombok.experimental.UtilityClass;

import java.util.List;

/** */
@UtilityClass
public class FakeRoundGroup {

    RoundGroup create(final Season season, final int roundGroupNumber, final List<Player> players) {
        final RoundGroup roundGroup = new RoundGroup(roundGroupNumber);
        int matchNumber = 1;
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                final Player firstPlayer = players.get(i);
                final Player secondPlayer = players.get(j);
                final Match fakeMatch = FakeMatch.create(season, firstPlayer, secondPlayer);
                fakeMatch.setNumber(matchNumber++);
                roundGroup.addMatch(fakeMatch);
            }
        }
        return roundGroup;
    }
}
