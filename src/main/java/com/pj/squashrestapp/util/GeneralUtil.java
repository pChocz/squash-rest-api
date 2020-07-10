package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class GeneralUtil {

  public static final TimeZone UTC_ZONE = TimeZone.getTimeZone("UTC");
  public static final ZoneId UTC_ZONE_ID = UTC_ZONE.toZoneId();

  public LocalDateTime toLocalDateTimeUtc(final Date date) {
    return LocalDateTime.ofInstant(
            date.toInstant(),
            UTC_ZONE_ID);
  }

  public LocalDateTime toLocalDateTimeUtc(final long epochSeconds) {
    return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(epochSeconds),
            UTC_ZONE_ID);
  }

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
