package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.model.Player;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class FakeUtil {

  public int randomBetweenTwoIntegers(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public List<Player> pickTwoRandomPlayers(final List<Player> list) {
    final List<Player> copy = new LinkedList<>(list);
    Collections.shuffle(copy);
    return copy.subList(0, 2);
  }

  public List<PlayerDto> pickThreeRandomPlayersFromTopFive(final List<PlayerDto> players) {
    final List<PlayerDto> copy = new LinkedList<>(players.subList(0, 5));
    Collections.shuffle(copy);
    return copy.subList(0, 3);
  }
}
