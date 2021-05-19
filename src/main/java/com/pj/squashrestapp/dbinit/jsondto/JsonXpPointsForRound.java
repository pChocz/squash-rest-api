package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
public class JsonXpPointsForRound {

  private String type;
  private String numberOfPlayersCsv;
  private List<String> pointsCsv;

  public int[] buildPlayerSplitArray() {
    return Arrays.stream(numberOfPlayersCsv.split(","))
            .map(String::trim)
            .mapToInt(Integer::valueOf)
            .toArray();
  }

  public int[][] buildXpPointsArray() {
    final int[][] array = new int[pointsCsv.size()][];
    int i = 0;
    for (final String string : pointsCsv) {
      final int[] groupPoints = Arrays.stream(string.split(","))
              .map(String::trim)
              .mapToInt(Integer::valueOf)
              .toArray();
      array[i++] = groupPoints;
    }
    return array;
  }

  public int extractNumberOfPlayers() {
    return Arrays.stream(this.buildPlayerSplitArray()).sum();
  }

}
