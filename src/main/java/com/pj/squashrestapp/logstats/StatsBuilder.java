package com.pj.squashrestapp.logstats;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatsBuilder {

  public static String buildStats(
      final LocalDate date, final List<PlayerLogStats> playerLogStatsList) {

    final StringBuilder sb =
        new StringBuilder("\n")
            .append(date)
            .append(" - daily stats:")
            .append("\n\n")
            .append("------------------------------------")
            .append("\n\n");

    for (final PlayerLogStats playerLogStats : playerLogStatsList) {
      sb.append(playerLogStats.getPlayer())
          .append("\n\tQUERIES:      ")
          .append(playerLogStats.getNumberOfQueries())
          .append("\n\tREQUESTS:     ")
          .append(playerLogStats.getNumberOfRequests())
          .append("\n\tDB QUERIES:   ")
          .append(playerLogStats.getNumberOfDatabaseQueries())
          .append("\n\tTIME MILLIS:  ")
          .append(playerLogStats.getTotalTimeMillis())
          .append("\n\n");
    }

    sb.append("------------------------------------");

    for (final PlayerLogStats playerLogStats : playerLogStatsList) {
      final List<LogEntry> allLogEntries = new ArrayList<>();
      allLogEntries.addAll(playerLogStats.getQueryLogEntries());
      allLogEntries.addAll(playerLogStats.getRestRequestLogEntries());
      allLogEntries.sort(Comparator.comparing(LogEntry::getTime));

      sb.append("\n\n").append(playerLogStats.getPlayer());
      for (final LogEntry entry : allLogEntries) {
        sb.append("\n\t").append(entry.buildMessage());
      }
    }

    sb.append("\n\n------------------------------------\n");

    return sb.toString();
  }
}
