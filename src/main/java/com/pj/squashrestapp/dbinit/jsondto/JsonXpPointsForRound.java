package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Data
@NoArgsConstructor
public class JsonXpPointsForRound {

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

}
