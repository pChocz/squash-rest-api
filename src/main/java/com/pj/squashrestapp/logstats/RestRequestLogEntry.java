package com.pj.squashrestapp.logstats;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

@Getter
@AllArgsConstructor
public class RestRequestLogEntry implements LogEntry {
  private final String player;
  private final String query;
  private final LocalTime time;
  private final long millis;
  private final int databaseQueries;

  @Override
  public String buildMessage() {
    return time + " | REST-REQUEST | " + databaseQueries + " | " + StringUtils.leftPad(String.valueOf(millis), 4) + "ms | " + query;
  }
}
