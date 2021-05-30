package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.logstats.LogFilenameDate;
import com.pj.squashrestapp.logstats.PlayerLogStats;
import com.pj.squashrestapp.service.PlayersQueriesStatsService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/app-stats")
@RequiredArgsConstructor
public class AppStatsController {

  private final PlayersQueriesStatsService playersQueriesStatsService;

  @GetMapping(value = "/log-files-dates")
  @PreAuthorize("isAdmin()")
  List<LogFilenameDate> getLogFilesDates() {
    final List<LogFilenameDate> logFilenameDates = playersQueriesStatsService.getLogFilenameDates();
    return logFilenameDates;
  }

  @GetMapping(value = "/daily-players-queries-stats/{file}")
  @PreAuthorize("isAdmin()")
  Set<PlayerLogStats> getDailyPlayersQueriesStatsFromFile(@PathVariable final String file) {
    final Set<PlayerLogStats> playersQueriesStats =
        playersQueriesStatsService.prepareStatsFromFile(file);
    return playersQueriesStats;
  }
}
