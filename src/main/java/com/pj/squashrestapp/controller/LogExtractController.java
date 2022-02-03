package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.model.LogEntry;
import com.pj.squashrestapp.repositorymongo.LogEntryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/log-extract")
@RequiredArgsConstructor
public class LogExtractController {

  private final LogEntryRepository logEntryRepository;

  @GetMapping
  List<LogEntry> getAllLogs() {
    return logEntryRepository.findAll();
  }
}
