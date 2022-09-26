package com.pj.squashrestapp.websocket;

import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.dto.scoreboard.RoundScoreboard;
import com.pj.squashrestapp.dto.scoreboard.SeasonScoreboardDto;
import com.pj.squashrestapp.service.MatchScoreService;
import com.pj.squashrestapp.service.ScoreboardService;
import com.pj.squashrestapp.service.SeasonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateWebsocketService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ScoreboardService scoreboardService;
    private final SeasonService seasonService;
    private final MatchScoreService matchScoreService;

    public void calculateAndBroadcastSeasonUpdate(final UUID seasonUuid) {
        log.info("Websocket - broadcasting season update -> " + seasonUuid);
        final SeasonScoreboardDto seasonScoreboard = seasonService.overalScoreboard(seasonUuid);
        messagingTemplate.convertAndSend("/season-scoreboard/" + seasonUuid, seasonScoreboard);
    }

    public void calculateAndBroadcastRoundUpdate(final UUID roundUuid) {
        log.info("Websocket - broadcasting round update -> " + roundUuid);
        final RoundScoreboard roundScoreboard = scoreboardService.buildScoreboardForRound(roundUuid);
        messagingTemplate.convertAndSend("/round-scoreboard/" + roundUuid, roundScoreboard);
    }

    public void calculateAndBroadcastMatchScoreUpdate(final UUID matchUuid) {
        log.info("Websocket - broadcasting match update -> " + matchUuid);
        final MatchDetailedDto match = matchScoreService.getMatchWithScores(matchUuid);
        messagingTemplate.convertAndSend("/match-score/" + matchUuid, match);
    }

    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        messagingTemplate.convertAndSend("/errors", exception.getMessage());
        return exception.getMessage();
    }

}
