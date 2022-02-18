package com.pj.squashrestapp.mongologs;

import com.pj.squashrestapp.util.GeneralUtil;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
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
@RequestMapping("/logs")
@RequiredArgsConstructor
@PreAuthorize("isAdmin()")
class LogExtractController {

  private final LogExtractService logExtractService;


  // All

  @DeleteMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  void deleteAllLogs() {
    logExtractService.deleteAll();
  }


  // Stats & Aggregates

  @GetMapping("summary")
  LogSummary getLogSummary(
          @RequestParam final int numberOfBuckets,
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

    return logExtractService.buildLogSummary(
            numberOfBuckets,
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
  }

//  @GetMapping("/stats")
//  LogsStats getLogsStats(
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> start,
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> stop,
//          @RequestParam final Optional<Boolean> isException,
//          @RequestParam final Optional<String> username,
//          @RequestParam final Optional<LogType> type,
//          @RequestParam final Optional<Long> durationMin,
//          @RequestParam final Optional<Long> durationMax,
//          @RequestParam final Optional<Long> queryCountMin,
//          @RequestParam final Optional<Long> queryCountMax,
//          @RequestParam final Optional<String> messageContains) {
//
//    final Criteria criteria = CriteriaForQueryBuilder.build(
//            start,
//            stop,
//            isException,
//            username,
//            type,
//            durationMin,
//            durationMax,
//            queryCountMin,
//            queryCountMax,
//            messageContains);
//
//    return logExtractService.buildStatsBasedOnQuery(criteria);
//  }

//  @GetMapping("/aggregate-by-user")
//  List<LogAggregateByUser> getLogsAggregateByUser(
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> start,
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> stop,
//          @RequestParam final Optional<Boolean> isException,
//          @RequestParam final Optional<String> username,
//          @RequestParam final Optional<LogType> type,
//          @RequestParam final Optional<Long> durationMin,
//          @RequestParam final Optional<Long> durationMax,
//          @RequestParam final Optional<Long> queryCountMin,
//          @RequestParam final Optional<Long> queryCountMax,
//          @RequestParam final Optional<String> messageContains) {
//
//    final Criteria criteria = CriteriaForQueryBuilder.build(
//            start,
//            stop,
//            isException,
//            username,
//            type,
//            durationMin,
//            durationMax,
//            queryCountMin,
//            queryCountMax,
//            messageContains);
//
//    return logExtractService.logAggregateByUser(criteria);
//  }

//  @GetMapping("/aggregate-by-method")
//  List<LogAggregateByMethod> getLogsAggregateByMethod(
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> start,
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> stop,
//          @RequestParam final Optional<Boolean> isException,
//          @RequestParam final Optional<String> username,
//          @RequestParam final Optional<LogType> type,
//          @RequestParam final Optional<Long> durationMin,
//          @RequestParam final Optional<Long> durationMax,
//          @RequestParam final Optional<Long> queryCountMin,
//          @RequestParam final Optional<Long> queryCountMax,
//          @RequestParam final Optional<String> messageContains) {
//
//    final Criteria criteria = CriteriaForQueryBuilder.build(
//            start,
//            stop,
//            isException,
//            username,
//            type,
//            durationMin,
//            durationMax,
//            queryCountMin,
//            queryCountMax,
//            messageContains);
//
//    return logExtractService.logAggregateByMethod(criteria);
//  }


  // LogEntries (pageable)

  @GetMapping()
  LogEntriesPaginated getLogs(
          @PageableDefault(sort = {"timestamp"}, direction = Sort.Direction.DESC, size = 200) final Pageable pageable,
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

    final Criteria criteria = CriteriaForQueryBuilder.build(
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

    return logExtractService.extractLogs(criteria, pageable);
  }


//  @GetMapping("/buckets")
//  Set<LogBucket> getLogBuckets(
//          @RequestParam final int numberOfBuckets,
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> start,
//          @RequestParam @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT) final Optional<Date> stop,
//          @RequestParam final Optional<Boolean> isException,
//          @RequestParam final Optional<String> username,
//          @RequestParam final Optional<LogType> type,
//          @RequestParam final Optional<Long> durationMin,
//          @RequestParam final Optional<Long> durationMax,
//          @RequestParam final Optional<Long> queryCountMin,
//          @RequestParam final Optional<Long> queryCountMax,
//          @RequestParam final Optional<String> messageContains) {
//
//    final Criteria criteria = CriteriaForQueryBuilder.build(
//            start,
//            stop,
//            isException,
//            username,
//            type,
//            durationMin,
//            durationMax,
//            queryCountMin,
//            queryCountMax,
//            messageContains);
//
//    return logExtractService.extractLogBuckets(criteria, start.orElseThrow(), stop.orElseThrow(), numberOfBuckets);
//  }

}
