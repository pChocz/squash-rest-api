package com.pj.squashrestapp.logstats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class PlayerLogStats implements Comparable<PlayerLogStats> {

  private final String player;
  private final int numberOfQueries;
  private final int numberOfRequests;
  private final int numberOfFrontendRefreshes;
  private final int numberOfDatabaseQueries;
  private final long totalTimeMillis;
  private final List<String> logEntriesMessages;
  @JsonIgnore private final List<LogEntry> logEntries;

  public PlayerLogStats(final List<LogEntry> logEntries) {
    logEntries.sort(Comparator.comparing(LogEntry::getTime).reversed());
    this.logEntries = logEntries;
    this.logEntriesMessages =
        logEntries.stream().map(LogEntry::buildMessage).collect(Collectors.toList());

    final List<RestRequestLogEntry> restRequestLogEntries = getRestRequestLogEntries();
    final List<QueryLogEntry> queryLogEntries = getQueryLogEntries();
    final List<FrontendLogEntry> frontendLogEntries = getFrontendLogEntries();

    this.player = logEntries.get(0).getPlayer();
    this.numberOfQueries = queryLogEntries.size();
    this.numberOfRequests = restRequestLogEntries.size();
    this.numberOfFrontendRefreshes = frontendLogEntries.size();

    this.numberOfDatabaseQueries =
        restRequestLogEntries.stream().mapToInt(RestRequestLogEntry::getDatabaseQueries).sum();

    this.totalTimeMillis =
        restRequestLogEntries.stream().mapToLong(RestRequestLogEntry::getMillis).sum();
  }

  private List<RestRequestLogEntry> getRestRequestLogEntries() {
    return logEntries.stream()
        .filter(RestRequestLogEntry.class::isInstance)
        .map(RestRequestLogEntry.class::cast)
        .collect(Collectors.toList());
  }

  private List<QueryLogEntry> getQueryLogEntries() {
    return logEntries.stream()
        .filter(QueryLogEntry.class::isInstance)
        .map(QueryLogEntry.class::cast)
        .collect(Collectors.toList());
  }

  private List<FrontendLogEntry> getFrontendLogEntries() {
    return logEntries.stream()
        .filter(FrontendLogEntry.class::isInstance)
        .map(FrontendLogEntry.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public int compareTo(final PlayerLogStats that) {
    return Comparator.comparingInt(PlayerLogStats::getNumberOfQueries)
        .thenComparingInt(PlayerLogStats::getNumberOfRequests)
        .reversed()
        .compare(this, that);
  }
}
