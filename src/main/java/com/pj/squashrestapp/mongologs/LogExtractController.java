package com.pj.squashrestapp.mongologs;

import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/log-extract")
@RequiredArgsConstructor
@PreAuthorize("isAdmin()")
class LogExtractController {

  private final LogExtractService logExtractService;

  @DeleteMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  void deleteAllLogs() {
    logExtractService.deleteAll();
  }

  // AGGREGATE

  @GetMapping("/aggregate-by-user")
  List<LogAggregateByUser> getLogsAggregateByUser() {
    return logExtractService.logAggregateByUser();
  }

  @GetMapping("/aggregate-by-method")
  List<LogAggregateByMethod> getLogsAggregateByMethod() {
    return logExtractService.logAggregateByMethod();
  }

  @GetMapping("/buckets")
  List<LogBucket> getLogBuckets(
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Date start,
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Date end,
      @RequestParam final int numberOfBuckets) {
    return logExtractService.extractLogBuckets(start, end, numberOfBuckets);
  }

  // LOGS PAGED

  @GetMapping("/all")
  LogEntriesPaginated getAllLogs(@PageableDefault(sort = {"timestamp"}, direction = Sort.Direction.DESC, size = 50) final Pageable pageable) {
    return logExtractService.extractLogs(new Query(), pageable);
  }

  @GetMapping()
  LogEntriesPaginated getFilteredLogs(
      @PageableDefault(sort = {"timestamp"}, direction = Sort.Direction.DESC, size = 50) final Pageable pageable,
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> start,
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> stop,
      @RequestParam final Optional<Boolean> isException,
      @RequestParam final Optional<String> username,
      @RequestParam final Optional<LogType> type,
      @RequestParam final Optional<Long> durationMin,
      @RequestParam final Optional<Long> durationMax,
      @RequestParam final Optional<Long> queryCountMin,
      @RequestParam final Optional<Long> queryCountMax,
      @RequestParam final Optional<String> messageContains) {

    final Query query = QueryBuilder.build(
        start,
        stop,
        isException,
        username,
        type,
        durationMin,
        durationMax,
        queryCountMin,
        queryCountMax,
        messageContains);

    return logExtractService.extractLogs(query, pageable);
  }


  // STATS

  @GetMapping("/all-stats")
  LogsStats getAllLogsStats() {
    return logExtractService.buildStatsBasedOnQuery(new Query());
  }

  @GetMapping("/stats")
  LogsStats getFilteredLogsStats(
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> start,
      @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> stop,
      @RequestParam final Optional<Boolean> isException,
      @RequestParam final Optional<String> username,
      @RequestParam final Optional<LogType> type,
      @RequestParam final Optional<Long> durationMin,
      @RequestParam final Optional<Long> durationMax,
      @RequestParam final Optional<Long> queryCountMin,
      @RequestParam final Optional<Long> queryCountMax,
      @RequestParam final Optional<String> messageContains) {

    final Query query = QueryBuilder.build(
        start,
        stop,
        isException,
        username,
        type,
        durationMin,
        durationMax,
        queryCountMin,
        queryCountMax,
        messageContains);

    return logExtractService.buildStatsBasedOnQuery(query);
  }
}
