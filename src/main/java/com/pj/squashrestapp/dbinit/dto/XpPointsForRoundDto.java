package com.pj.squashrestapp.dbinit.dto;

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
@Getter
@NoArgsConstructor
@Root(name = "xpPointsForRound")
public class XpPointsForRoundDto {

  @Element
  private String numberOfPlayersCsv;

  @ElementList(inline = true, entry = "pointsCsv")
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
