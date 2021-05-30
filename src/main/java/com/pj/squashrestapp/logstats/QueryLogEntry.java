package com.pj.squashrestapp.logstats;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QueryLogEntry implements LogEntry {

  @JsonFormat(pattern = GeneralUtil.TIME_FORMAT)
  private final LocalTime time;

  private final String player;
  private final String query;

  @Override
  public String buildMessage() {
    return time + " | QUERY        | " + query;
  }
}
