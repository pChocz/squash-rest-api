package com.pj.squashrestapp.logstats;

import java.util.List;
import lombok.Getter;

@Getter
public class PlayerLogStats {

  private final List<QueryLogEntry> queryLogEntries;
  private final List<RestRequestLogEntry> restRequestLogEntries;
  private final String player;
  private final int numberOfQueries;
  private final int numberOfRequests;
  private final int numberOfDatabaseQueries;
  private final long totalTimeMillis;

  public PlayerLogStats(
      final List<QueryLogEntry> queryLogEntries,
      final List<RestRequestLogEntry> restRequestLogEntries) {
    this.queryLogEntries = queryLogEntries;
    this.restRequestLogEntries = restRequestLogEntries;

    this.player = restRequestLogEntries.get(0).getPlayer();
    this.numberOfQueries = queryLogEntries.size();
    this.numberOfRequests = restRequestLogEntries.size();

    this.numberOfDatabaseQueries =
        restRequestLogEntries.stream().mapToInt(RestRequestLogEntry::getDatabaseQueries).sum();

    this.totalTimeMillis =
        restRequestLogEntries.stream().mapToLong(RestRequestLogEntry::getMillis).sum();
  }
}
