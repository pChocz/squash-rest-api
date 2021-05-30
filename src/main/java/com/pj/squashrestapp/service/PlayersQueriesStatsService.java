package com.pj.squashrestapp.service;

import com.pj.squashrestapp.logstats.LogEntry;
import com.pj.squashrestapp.logstats.LogFilenameDate;
import com.pj.squashrestapp.logstats.PlayerLogStats;
import com.pj.squashrestapp.logstats.QueryLogEntry;
import com.pj.squashrestapp.logstats.RestRequestLogEntry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 *
 * <pre>
 * - Example line of QUERY log entry:
 * 14:56:24.172 |  INFO | c.p.s.aspects.LogQueryAspect   | QUERY         |  Maniak  |  Round Scoreboard - R: 7 | S: 9 | Dziadoliga
 * (1)             (2)    (3)                                               (4)        (5)
 *
 * - Example line of REST-REQUEST log entry:
 * 14:14:01.708 |  INFO | c.p.s.a.LogControllerAspect    | REST-REQUEST  4  Maniak  105ms  ScoreboardController.scoreboardForMostRecentRoundOfPlayer[73992a9c-fea3-4a24-a95b-91e1e840c26a]
 * (1)             (2)    (3)                                           (4) (5)     (6)    (7)
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayersQueriesStatsService {

  private static final Pattern REST_REQUEST_PATTERN =
      Pattern.compile("(.+?)[|](.+?)[|](.+?)[|].+REST-REQUEST.+?(\\d+)(.+?)(\\d+)ms(.+)");

  private static final Pattern QUERY_PATTERN =
      Pattern.compile("(.+?)[|](.+?)[|](.+?)[|].+QUERY.+?[|](.+?)[|](.+)");

  private static final Pattern ROLLING_LOG_FILENAME_PATTERN = Pattern.compile(".*out\\.(.*)\\.log");

  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final String LOGS_FOLDER = "logs";
  private static final String DAILY_STATS_LOG = "daily-stats.log";
  private static final String REST_REQUEST_PREFIX = "REST-REQUEST";
  private static final String QUERY_PREFIX = "QUERY";

  public Set<PlayerLogStats> prepareStatsFromFile(final String file) {
    final Path path = Paths.get(LOGS_FOLDER, file);

    try {
      final List<String> logLines = Files.readAllLines(path);
      final Set<PlayerLogStats> playerLogStatsSet = parseLogLines(logLines);
      return playerLogStatsSet;

    } catch (final IOException e) {
      log.error("Cannot read log file [{}]", file);
      return null;
    }
  }

  private Set<PlayerLogStats> parseLogLines(final List<String> logLines) {
    final List<LogEntry> logEntries = new ArrayList<>();
    for (final String logLine : logLines) {
      if (logLine.contains(REST_REQUEST_PREFIX)) {
        final RestRequestLogEntry entry = parseRestRequestEntry(logLine);
        if (entry != null) {
          logEntries.add(entry);
        }
      } else if (logLine.contains(QUERY_PREFIX)) {
        final QueryLogEntry entry = parseQueryEntry(logLine);
        if (entry != null) {
          logEntries.add(entry);
        }
      }
    }

    return logEntries.stream()
        .map(LogEntry::getPlayer)
        .distinct()
        .map(
            player ->
                logEntries.stream()
                    .filter(entry -> entry.getPlayer().equals(player))
                    .collect(Collectors.toList()))
        .map(PlayerLogStats::new)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  private RestRequestLogEntry parseRestRequestEntry(final String logLine) {
    final Matcher matcher = REST_REQUEST_PATTERN.matcher(logLine);
    if (matcher.matches()) {
      return new RestRequestLogEntry(
          LocalTime.parse(matcher.group(1).trim(), TIME_FORMATTER),
          Integer.parseInt(matcher.group(4).trim()),
          matcher.group(5).trim(),
          Long.parseLong(matcher.group(6).trim()),
          matcher.group(7).trim());
    }
    return null;
  }

  private QueryLogEntry parseQueryEntry(final String logLine) {
    final Matcher matcher = QUERY_PATTERN.matcher(logLine);
    if (matcher.matches()) {
      return new QueryLogEntry(
          LocalTime.parse(matcher.group(1).trim(), TIME_FORMATTER),
          matcher.group(4).trim(),
          matcher.group(5).trim());
    }
    return null;
  }

  public List<LogFilenameDate> getLogFilenameDates() {
    final List<LogFilenameDate> logFilenameDates = new ArrayList<>();
    final File logDirectory = new File(LOGS_FOLDER);
    for (final File file : logDirectory.listFiles()) {
      final Matcher matcher = ROLLING_LOG_FILENAME_PATTERN.matcher(file.getName());
      if (matcher.matches()) {
        final String dateAsString = matcher.group(1);
        try {
          final LocalDate date = LocalDate.parse(dateAsString, DATE_FORMATTER);
          logFilenameDates.add(new LogFilenameDate(date, file.getName()));
        } catch (final DateTimeParseException e) {
          log.error("Cannot parse date {}", dateAsString);
        }
      }
    }
    logFilenameDates.add(new LogFilenameDate(LocalDate.now(), "out.log"));
    logFilenameDates.sort(Comparator.comparing(LogFilenameDate::getDate).reversed());
    return logFilenameDates;
  }
}
