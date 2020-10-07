package com.pj.squashrestapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pj.squashrestapp.config.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 */
@Slf4j
@UtilityClass
public class TimeLogUtil {

  public void logFinish(final long startTime) {
    final double durationSecondsRounded = getDurationSecondsRounded(startTime);
    log.info("{} seconds", durationSecondsRounded);
  }

  private double getDurationSecondsRounded(final long startTime) {
    final long endTime = System.nanoTime();
    final long duration = endTime - startTime;
    final double durationSeconds = (double) duration / 1_000_000_000;
    return (double) Math.round(durationSeconds * 100d) / 100d;
  }

  public void logQuery(final long startTime, final String query) {
    final String username = GeneralUtil.extractSessionUsername();
    final double durationSecondsRounded = getDurationSecondsRounded(startTime);
    final String durationFormatted = getFormatted(durationSecondsRounded);
    log.info("QUERY: {}\t{}\t{}", username, durationFormatted, query);
  }

  private String getFormatted(final double durationSecondsRounded) {
    final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    final DecimalFormat decimalFormat = new DecimalFormat();
    decimalFormat.setDecimalFormatSymbols(symbols);
    decimalFormat.setMinimumFractionDigits(2);
    decimalFormat.setMaximumFractionDigits(2);
    return decimalFormat.format(durationSecondsRounded);
  }

//  public <T> void logFinish(final long startTime, final T objectToPrint) {
//    try {
//      final ObjectMapper mapper = new ObjectMapper();
//      log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectToPrint));
//
//      final double durationSecondsRounded = getDurationSecondsRounded(startTime);
//      log.info("{} seconds\n", durationSecondsRounded);
//
//    } catch (final JsonProcessingException e) {
//      log.error("not serializable object", e);
//    }
//  }
//
//  public <T> void logFinish(final long startTime, final int counter, final T objectToPrint) {
//    try {
//      final ObjectMapper mapper = new ObjectMapper();
//      log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectToPrint));
//
//      final double durationSecondsRounded = getDurationSecondsRounded(startTime);
//      log.info("finished fetching {} items", counter);
//      log.info("{} seconds\n", durationSecondsRounded);
//
//    } catch (final JsonProcessingException e) {
//      log.error("not serializable object", e);
//    }
//  }

}
