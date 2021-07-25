package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Player;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class FakeBonusPoints {

  public List<BonusPoint> create(
      final List<Player> players,
      final LocalDate date,
      final int minOccurs,
      final int maxOccurs,
      final int minPoints,
      final int maxPoints) {

    final List<BonusPoint> bonusPoints = new ArrayList<>();
    final int occurs = FakeUtil.randomBetweenTwoIntegers(minOccurs, maxOccurs);

    for (int i = 0; i < occurs; i++) {
      final int points = FakeUtil.randomBetweenTwoIntegers(minPoints, maxPoints);
      final List<Player> twoRandomPlayers = FakeUtil.pickTwoRandomPlayers(players);
      bonusPoints.add(
          new BonusPoint(twoRandomPlayers.get(0), twoRandomPlayers.get(1), points, date));
    }

    return bonusPoints;
  }
}
