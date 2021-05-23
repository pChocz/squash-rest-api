package com.pj.squashrestapp.util;

import com.pj.squashrestapp.config.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class GeneralUtil {

  public static final UUID ADMIN_UUID = UUID.fromString("73992a9c-fea3-4a24-a95b-91e1e840c26a");
  public static final String DECIMAL_FORMAT = "###,###.#";
  public static final char DOT_DECIMAL_SEPARATOR = '.';
  public static final char SPACE_GROUPING_SEPARATOR = ' ';
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
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

  public String buildProperUsername(final String username) {
    return capitalize(username
            .trim()
            .replaceAll(" +", " "));
  }

  private String capitalize(final String string) {
    final int sl = string.length();
    final StringBuilder sb = new StringBuilder(sl);
    boolean lod = false;
    for (int s = 0; s < sl; s++) {
      final int cp = string.codePointAt(s);
      sb.appendCodePoint(lod ? Character.toLowerCase(cp) : Character.toUpperCase(cp));
      lod = Character.isLetterOrDigit(cp);
      if (!Character.isBmpCodePoint(cp)) {
        s++;
      }
    }
    return sb.toString();
  }

  public String extractSessionUsername() {
    final Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (userDetails instanceof UserDetailsImpl) {
      return ((UserDetailsImpl) userDetails).getUsername();
    } else {
      return "ANONYMOUS";
    }
  }

  public UUID extractSessionUserUuid() {
    final UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userDetails.getUuid();
  }

  public double getDurationSecondsRounded(final long startTime) {
    final long endTime = System.nanoTime();
    final long duration = endTime - startTime;
    final double durationSeconds = (double) duration / 1_000_000_000;
    return (double) Math.round(durationSeconds * 100d) / 100d;
  }

}
