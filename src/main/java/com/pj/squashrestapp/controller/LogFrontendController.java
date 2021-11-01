package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.util.GeneralUtil;
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

  @PostMapping
  void post(@RequestParam final String message) {
    final String username = GeneralUtil.extractSessionUsername();
    log.info("FRONTEND-LOG  |  {}  |  {}", username, message);
  }
}
