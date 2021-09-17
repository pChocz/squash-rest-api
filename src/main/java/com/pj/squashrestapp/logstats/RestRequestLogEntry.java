package com.pj.squashrestapp.logstats;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public class RestRequestLogEntry implements LogEntry {

  @JsonFormat(pattern = GeneralUtil.TIME_FORMAT)
  private final LocalTime time;

  private final int databaseQueries;
  private final String player;
  private final long millis;
  private final String query;

  @Override
  public String buildMessage() {
    return time
        + " | REST-REQUEST | "
        + databaseQueries
        + " | "
        + StringUtils.leftPad(String.valueOf(millis), 4)
        + "ms | "
        + query;
  }
}
