package com.pj.squashrestapp.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.LogEntry;
import com.pj.squashrestapp.repositorymongo.LogEntryRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/log-extract")
@RequiredArgsConstructor
public class LogExtractController {

  private final LogEntryRepository logEntryRepository;
  private final MongoTemplate mongoTemplate;

  @GetMapping
  List<LogEntry> getAllLogs() {
    return logEntryRepository.findAll();
  }

//  All fields of LogEntry:
//    private String id;
//    private LocalDateTime timestamp;
//    private String username;
//    private String className;
//    private String methodName;
//    private String arguments;
//    private Long duration;
//    private long queryCount;
//    private String errorMessage;
//    private String stackTrace;
//    private String message;
//    private String type;

//  @GetMapping
//  List<LogEntry> getFilteredLogs(
//      @RequestParam(required = false) @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_FORMAT) final Optional<LocalDateTime> start,
//      @RequestParam(required = false) @DateTimeFormat(pattern = GeneralUtil.DATE_TIME_FORMAT) final Optional<LocalDateTime> stop,
//      @RequestParam(required = false) final Optional<String> username,
//      @RequestParam(required = false) final Optional<String> type,
//      @RequestParam(required = false) final Optional<Long> durationMin,
//      @RequestParam(required = false) final Optional<Long> durationMax,
//      @RequestParam(required = false) final Optional<String> messagePart) {
//
//    final Query query = new Query();
//
//    if (username.isPresent()) {
//      query.addCriteria(Criteria.where("username").is(username.get()));
//    }
//
//    if (type.isPresent()) {
//      query.addCriteria(Criteria.where("type").is(type.get()));
//    }
//
//    List<LogEntry> logEntries = mongoTemplate.find(query, LogEntry.class);
//
//    return logEntries;
//  }


}














