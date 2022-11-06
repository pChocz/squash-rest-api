package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.enums.AdditionalMatchType;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Player;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** */
@UtilityClass
public class FakeAdditionalMatches {

    public List<AdditionalMatch> create(
            final League league,
            final int seasonNumber,
            final List<Player> players,
            final LocalDate date,
            final int minOccurs,
            final int maxOccurs) {

        final List<AdditionalMatch> additionalMatches = new ArrayList<>();
        final int occurs = FakeUtil.randomBetweenTwoIntegers(minOccurs, maxOccurs);

        for (int i = 0; i < occurs; i++) {
            final List<Player> twoRandomPlayers = FakeUtil.pickTwoRandomPlayers(players);
            additionalMatches.add(FakeMatch.createAdditional(
                    league,
                    seasonNumber,
                    twoRandomPlayers.get(0),
                    twoRandomPlayers.get(1),
                    date.plusDays((i + 1) * 2L),
                    AdditionalMatchType.FRIENDLY));
        }

        return additionalMatches;
    }
}
