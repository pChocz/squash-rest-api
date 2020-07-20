package com.pj.squashrestapp.dbinit.fake;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.Player;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 *
 */
@SuppressWarnings("MagicNumber")
@UtilityClass
public class FakePlayersSelector {

  public ArrayListMultimap<Integer, Player> select(final List<Player> allPlayers) {

    final List<Player> group1stPlayers;
    final List<Player> group2ndPlayers;
    final List<Player> group3rdPlayers;
    final ArrayListMultimap<Integer, Player> returnMap = ArrayListMultimap.create();

    final int numberOfPlayers = allPlayers.size();

    switch (numberOfPlayers) {

      case 4, 5, 6, 7 -> {
        group1stPlayers = allPlayers;
        returnMap.putAll(1, group1stPlayers);
      }

      case 8, 9 -> {
        group1stPlayers = allPlayers.subList(0, 4);
        group2ndPlayers = allPlayers.subList(4, numberOfPlayers);
        returnMap.putAll(1, group1stPlayers);
        returnMap.putAll(2, group2ndPlayers);
      }

      case 10, 11 -> {
        group1stPlayers = allPlayers.subList(0, 5);
        group2ndPlayers = allPlayers.subList(5, numberOfPlayers);
        returnMap.putAll(1, group1stPlayers);
        returnMap.putAll(2, group2ndPlayers);
      }

      case 12, 13 -> {
        group1stPlayers = allPlayers.subList(0, 6);
        group2ndPlayers = allPlayers.subList(6, numberOfPlayers);
        returnMap.putAll(1, group1stPlayers);
        returnMap.putAll(2, group2ndPlayers);
      }

      case 14, 15 -> {
        group1stPlayers = allPlayers.subList(0, 5);
        group2ndPlayers = allPlayers.subList(5, 10);
        group3rdPlayers = allPlayers.subList(10, numberOfPlayers);
        returnMap.putAll(1, group1stPlayers);
        returnMap.putAll(2, group2ndPlayers);
        returnMap.putAll(3, group3rdPlayers);
      }

      case 16 -> {
        group1stPlayers = allPlayers.subList(0, 6);
        group2ndPlayers = allPlayers.subList(6, 11);
        group3rdPlayers = allPlayers.subList(11, numberOfPlayers);
        returnMap.putAll(1, group1stPlayers);
        returnMap.putAll(2, group2ndPlayers);
        returnMap.putAll(3, group3rdPlayers);
      }
    }

    return returnMap;
  }

}
