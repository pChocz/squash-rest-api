package com.pj.squashrestapp.dbinit.fake;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
@UtilityClass
public class FakeSeason {

  public Season create(final int seasonNumber,
                       final LocalDate seasonStartDate,
                       final int numberOfRounds,
                       final List<Player> allPlayers,
                       final int minNumberOfAttendingPlayers,
                       final int maxNumberOfAttendingPlayers) {

    final Season season = new Season(seasonNumber, seasonStartDate);

    LocalDate roundDate = seasonStartDate;
    for (int roundNumber = 1; roundNumber <= numberOfRounds; roundNumber++) {
      Collections.shuffle(allPlayers);
      final int numberOfRoundPlayers = randBetween(minNumberOfAttendingPlayers, maxNumberOfAttendingPlayers);
      final List<Player> roundPlayers = allPlayers.subList(0, numberOfRoundPlayers);
      final ArrayListMultimap<Integer, Player> attendingPlayersGrouped = FakePlayersSelector.select(roundPlayers);
      final Round round = FakeRound.create(roundNumber, roundDate, attendingPlayersGrouped);
      season.addRound(round);

      final List<BonusPoint> bonusPoints = FakeBonusPoints.create(roundPlayers,
              0, 3,
              1, 3);

      for (final BonusPoint bonusPoint : bonusPoints) {
        season.addBonusPoint(bonusPoint);
      }

      roundDate = roundDate.plusWeeks(1);
    }

    return season;
  }

  private int randBetween(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

}
