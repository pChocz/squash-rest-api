package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class GeneralUtil {

  public String intArrayToString(final int[] intArray) {
    return integerListToString(
            intArrayToList(intArray));
  }

  /**
   * Converts list of Integer to nicely formatted String,
   * ex: 1 | 3 | 4
   *
   * @param integerList list of integers to format
   * @return nicely formatted String
   */
  public String integerListToString(final List<Integer> integerList) {
    return integerList
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining(" | "));
  }

  public List<Integer> intArrayToList(final int[] integerList) {
    return Arrays
            .stream(integerList)
            .boxed()
            .collect(Collectors.toList());
  }

}
