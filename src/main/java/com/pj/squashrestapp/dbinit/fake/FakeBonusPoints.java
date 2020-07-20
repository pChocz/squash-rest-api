package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Player;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
@UtilityClass
public class FakeBonusPoints {

  public List<BonusPoint> create(final List<Player> players,
                                 final int minOccurs,
                                 final int maxOccurs,
                                 final int minPoints,
                                 final int maxPoints) {

    final List<BonusPoint> bonusPoints = new ArrayList<>();
    final int occurs = randBetween(minOccurs, maxOccurs);

    for (int i = 0; i < occurs; i++) {
      final int points = randBetween(minPoints, maxPoints);
      final List<Player> twoRandomPlayers = pickTwoRandomPlayers(players);
      bonusPoints.add(new BonusPoint(twoRandomPlayers.get(0), points));
      bonusPoints.add(new BonusPoint(twoRandomPlayers.get(1), -points));
    }

    return bonusPoints;
  }

  private int randBetween(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  private List<Player> pickTwoRandomPlayers(final List<Player> list) {
    final List<Player> copy = new LinkedList<Player>(list);
    Collections.shuffle(copy);
    return copy.subList(0, 2);
  }

}
