package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.aspects.QueryLog;
import com.pj.squashrestapp.dto.match.MatchesSimplePaginated;
import com.pj.squashrestapp.dto.scoreboard.HeadToHeadScoreboard;
import com.pj.squashrestapp.dto.scoreboard.PlayerSummary;
import com.pj.squashrestapp.dto.scoreboard.Scoreboard;
import com.pj.squashrestapp.service.HeadToHeadScoreboardService;
import com.pj.squashrestapp.service.PlayersScoreboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/head-to-head")
@RequiredArgsConstructor
public class HeadToHeadScoreboardController {

  private final HeadToHeadScoreboardService headToHeadScoreboardService;


  @GetMapping(value = "/{firstPlayerUuid}/{secondPlayerUuid}")
  @ResponseBody
  @QueryLog
  HeadToHeadScoreboard headToHead(@PathVariable final UUID firstPlayerUuid,
                                  @PathVariable final UUID secondPlayerUuid) {
    final HeadToHeadScoreboard scoreboard = headToHeadScoreboardService.build(firstPlayerUuid, secondPlayerUuid);
    return scoreboard;
  }

}
