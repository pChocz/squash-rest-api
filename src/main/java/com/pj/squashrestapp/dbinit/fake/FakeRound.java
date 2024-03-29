package com.pj.squashrestapp.dbinit.fake;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** */
@UtilityClass
public class FakeRound {

    public Round create(
            final Season season,
            final int roundNumber,
            final LocalDate roundDate,
            final ArrayListMultimap<Integer, Player> roundGroupNumberToPlayersMultimap) {

        final Round round = new Round(roundNumber, roundDate);

        final Set<Integer> roundGroupsNumbers = roundGroupNumberToPlayersMultimap.keySet();

        for (final int roundGroupNumber : roundGroupsNumbers) {
            final List<Player> roundGroupPlayers = roundGroupNumberToPlayersMultimap.get(roundGroupNumber);
            final RoundGroup roundGroup = FakeRoundGroup.create(season, roundGroupNumber, roundGroupPlayers);
            round.addRoundGroup(roundGroup);
        }

        final List<Integer> countPerRound = roundGroupNumberToPlayersMultimap.keySet().stream()
                .map(roundGroupNumberToPlayersMultimap::get)
                .map(List::size)
                .collect(Collectors.toList());

        round.setSplit(GeneralUtil.integerListToString(countPerRound));
        round.setFinished(true);

        return round;
    }
}
