package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.LogEntry;
import com.pj.squashrestapp.repositorymongo.LogEntryRepository;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/frontend-logs")
@RequiredArgsConstructor
public class LogFrontendController {

  private final LogEntryRepository logEntryRepository;

  @PostMapping
  void post(@RequestParam final String message) {
    final String username = GeneralUtil.extractSessionUsername();
    log.info("FRONTEND-LOG  |  {}  |  {}", username, message);
    final LogEntry logEntry = new LogEntry();
    logEntry.setTimestamp(LocalDateTime.now());
    logEntry.setUsername(username);
    logEntry.setMessage(message);
    logEntry.setType("FRONTEND");
    logEntryRepository.save(logEntry);
  }
}
