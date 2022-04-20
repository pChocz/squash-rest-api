package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dto.scoreboard.headtohead.HeadToHeadScoreboard;
import com.pj.squashrestapp.service.HeadToHeadScoreboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/** */
@Slf4j
@RestController
@RequestMapping("/head-to-head")
@RequiredArgsConstructor
public class HeadToHeadScoreboardController {

    private final HeadToHeadScoreboardService headToHeadScoreboardService;

    @GetMapping(value = "/{firstPlayerUuid}/{secondPlayerUuid}")
    HeadToHeadScoreboard getHeadToHeadStatistics(
            @PathVariable final UUID firstPlayerUuid,
            @PathVariable final UUID secondPlayerUuid,
            @RequestParam(defaultValue = "true") final boolean includeAdditional) {
        List<UUID> uuids = Stream.of(firstPlayerUuid, secondPlayerUuid).sorted().toList();
        final HeadToHeadScoreboard scoreboard =
                headToHeadScoreboardService.build(uuids.get(0), uuids.get(1), includeAdditional);
        return scoreboard;
    }
}
