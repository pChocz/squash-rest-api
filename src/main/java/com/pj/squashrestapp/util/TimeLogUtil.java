package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TimeLogUtil {

  public void logFinish(final long startTime, final int counter) {
    final double durationSecondsRounded = getDurationSecondsRounded(startTime);
    log.info("finished fetching {} items", counter);
    log.info("{} seconds", durationSecondsRounded);
  }

  private double getDurationSecondsRounded(final long startTime) {
    final long endTime = System.nanoTime();
    final long duration = endTime - startTime;
    final double durationSeconds = (double) duration / 1_000_000_000;
    return (double) Math.round(durationSeconds * 100d) / 100d;
  }

  public void logFinish(final long startTime) {
    final double durationSecondsRounded = getDurationSecondsRounded(startTime);
    log.info("{} seconds took", durationSecondsRounded);
  }

}
