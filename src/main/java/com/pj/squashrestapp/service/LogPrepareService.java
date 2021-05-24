package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.email.EmailSendConfig;
import com.pj.squashrestapp.logstats.LogEntry;
import com.pj.squashrestapp.logstats.PlayerLogStats;
import com.pj.squashrestapp.logstats.QueryLogEntry;
import com.pj.squashrestapp.logstats.RestRequestLogEntry;
import com.pj.squashrestapp.logstats.StatsBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogPrepareService {


  private static final Pattern REST_REQUEST_PATTERN =
      Pattern.compile("(.+?)[|](.+?)[|](.+?)[|](.+)REST-REQUEST.+?(\\d)(.+?)(\\d+)ms(.+)");

  private static final Pattern QUERY_PATTERN =
      Pattern.compile("(.+?)[|](.+?)[|](.+?)[|](.+?)[|](.+?)[|](.+)");

  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final String LOGS_FOLDER = "logs";
  private static final String DAILY_STATS_LOG = "daily-stats.log";
  private static final String REST_REQUEST_PREFIX = "REST-REQUEST";
  private static final String QUERY_PREFIX = "QUERY";

  public void prepareDailyStatsForYesterday() {
    final LocalDate yesterdayDate = LocalDate.now().minusDays(1);
    final String logFileName = "out." + yesterdayDate.format(DATE_FORMATTER) + ".log";
    final Path path = Paths.get(LOGS_FOLDER, logFileName);

    try {
      final List<String> logLines = Files.readAllLines(path);
      parseLogLines(logLines, yesterdayDate);
      log.info("Created and saved stats for date [{}]", yesterdayDate.format(DATE_FORMATTER));

    } catch (final IOException e) {
      log.error("Cannot read log file [{}]", logFileName);
    }
  }

  private void parseLogLines(final List<String> logLines, final LocalDate date) {
    final List<RestRequestLogEntry> restRequests = new ArrayList<>();
    final List<QueryLogEntry> queries = new ArrayList<>();
    for (final String logLine : logLines) {
      if (logLine.contains(REST_REQUEST_PREFIX)) {
        final RestRequestLogEntry entry = parseRestRequestEntry(logLine);
        restRequests.add(entry);
      } else if (logLine.contains(QUERY_PREFIX)) {
        final QueryLogEntry entry = parseQueryEntry(logLine);
        queries.add(entry);
      }
    }

    final List<String> players =
        restRequests.stream().map(LogEntry::getPlayer).distinct().collect(Collectors.toList());

    final List<PlayerLogStats> playerLogStatsList = new ArrayList<>();
    for (final String player : players) {

      final List<QueryLogEntry> queriesForPlayer =
          queries.stream()
              .filter(entry -> entry.getPlayer().equals(player))
              .collect(Collectors.toList());

      final List<RestRequestLogEntry> restRequestsForPlayer =
          restRequests.stream()
              .filter(entry -> entry.getPlayer().equals(player))
              .collect(Collectors.toList());

      playerLogStatsList.add(new PlayerLogStats(queriesForPlayer, restRequestsForPlayer));
    }

    final String statsContent = StatsBuilder.buildStats(date, playerLogStatsList);
    writeToFile(statsContent);
  }

  private RestRequestLogEntry parseRestRequestEntry(final String logLine) {
    final Matcher matcher = REST_REQUEST_PATTERN.matcher(logLine);
    if (matcher.matches()) {
      return new RestRequestLogEntry(
          matcher.group(6).trim(),
          matcher.group(8).trim(),
          LocalTime.parse(matcher.group(1).trim(), TIME_FORMATTER),
          Long.parseLong(matcher.group(7).trim()),
          Integer.parseInt(matcher.group(5).trim()));
    }
    return null;
  }

  private QueryLogEntry parseQueryEntry(final String logLine) {
    final Matcher matcher = QUERY_PATTERN.matcher(logLine);
    if (matcher.matches()) {
      return new QueryLogEntry(
          matcher.group(5).trim(),
          matcher.group(6).trim(),
          LocalTime.parse(matcher.group(1).trim(), TIME_FORMATTER));
    }
    return null;
  }

  private void writeToFile(final String content) {
    try {
      final byte[] contentBytes = content.getBytes();
      final Path path = Paths.get(LOGS_FOLDER, DAILY_STATS_LOG);
      Files.write(
          path,
          contentBytes,
          StandardOpenOption.WRITE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.CREATE);
    } catch (final IOException e) {
      log.error("Cannot create daily stats log file!", e);
    }
  }
}
