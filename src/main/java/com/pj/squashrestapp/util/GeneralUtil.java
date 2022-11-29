package com.pj.squashrestapp.util;

import com.pj.squashrestapp.config.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
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

/** */
@UtilityClass
public class GeneralUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_TIME_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final TimeZone UTC_ZONE = TimeZone.getTimeZone("UTC");
    public static final ZoneId UTC_ZONE_ID = UTC_ZONE.toZoneId();

    public LocalDateTime toLocalDateTimeUtc(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), UTC_ZONE_ID);
    }

    public LocalDateTime toLocalDateTimeUtc(final long epochSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), UTC_ZONE_ID);
    }

    public String intArrayToString(final int[] intArray) {
        return integerListToString(intArrayToList(intArray));
    }

    /**
     * Converts list of Integer to nicely formatted String, ex: 1 | 3 | 4
     *
     * @param integerList list of integers to format
     * @return nicely formatted String
     */
    public String integerListToString(final List<Integer> integerList) {
        return integerList.stream().map(Object::toString).collect(Collectors.joining(" | "));
    }

    public List<Integer> intArrayToList(final int[] integerList) {
        return Arrays.stream(integerList).boxed().toList();
    }

    public int splitToSum(final String split) {
        return Arrays.stream(split.split("\\|"))
                .map(String::trim)
                .mapToInt(Integer::valueOf)
                .sum();
    }

    public String buildProperUsername(final String username) {
        return capitalize(username.trim().replaceAll(" +", " "));
    }

    private String capitalize(final String string) {
        final int sl = string.length();
        final StringBuilder sb = new StringBuilder(sl);
        boolean lod = false;
        for (int s = 0; s < sl; s++) {
            final int cp = string.codePointAt(s);
            sb.appendCodePoint(lod ? Character.toLowerCase(cp) : Character.toUpperCase(cp));
            lod = Character.isLetterOrDigit(cp);
            if (!Character.isBmpCodePoint(cp)) s++;
        }
        return sb.toString();
    }

    public String extractSessionUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "ANONYMOUS";
        }

        final Object userDetailsObject = authentication.getPrincipal();
        if (userDetailsObject instanceof UserDetailsImpl userDetails) {
            return userDetails.isAdminLogin()
                    ? "[ADMIN] " + userDetails.getUsername()
                    : userDetails.getUsername();
        } else {
            return "ANONYMOUS";
        }
    }

    public UserDetailsImpl extractSessionUser() {
        return (UserDetailsImpl)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UUID extractSessionUserUuid() {
        final Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserDetailsImpl userDetailsImpl) {
            return userDetailsImpl.getUuid();
        }
        return new UUID(0L, 0L);
    }

    public double getDurationSecondsRounded(final long startTime) {
        final long endTime = System.nanoTime();
        final long duration = endTime - startTime;
        final double durationSeconds = (double) duration / 1_000_000_000;
        return Math.round(durationSeconds * 100d) / 100d;
    }
}
