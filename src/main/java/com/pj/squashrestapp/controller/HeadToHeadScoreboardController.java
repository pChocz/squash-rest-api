package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.QueryLog;
import com.pj.squashrestapp.dto.scoreboard.headtohead.HeadToHeadScoreboard;
import com.pj.squashrestapp.service.HeadToHeadScoreboardService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** */
@Slf4j
@RestController
@RequestMapping("/head-to-head")
@RequiredArgsConstructor
public class HeadToHeadScoreboardController {

  private final HeadToHeadScoreboardService headToHeadScoreboardService;

  @GetMapping(value = "/{firstPlayerUuid}/{secondPlayerUuid}")
  @ResponseBody
  @QueryLog
  HeadToHeadScoreboard headToHead(
      @PathVariable final UUID firstPlayerUuid,
      @PathVariable final UUID secondPlayerUuid,
      @RequestParam(defaultValue = "true") final boolean includeAdditional) {
    log.info("{}", includeAdditional);
    final HeadToHeadScoreboard scoreboard =
        headToHeadScoreboardService.build(firstPlayerUuid, secondPlayerUuid, includeAdditional);
    return scoreboard;
  }
}
