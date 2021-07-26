package com.pj.squashrestapp.logstats;

import java.time.LocalTime;

public interface LogEntry {
  String buildMessage();

  String getPlayer();

  String getQuery();

  LocalTime getTime();
}
