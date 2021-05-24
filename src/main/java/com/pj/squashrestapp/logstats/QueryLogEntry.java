package com.pj.squashrestapp.logstats;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueryLogEntry implements LogEntry {
  private final String player;
  private final String query;
  private final LocalTime time;

  @Override
  public String buildMessage() {
    return time + " | QUERY        | " + query;
  }
}
