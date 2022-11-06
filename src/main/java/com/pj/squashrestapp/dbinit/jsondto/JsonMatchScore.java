package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.enums.AppealDecision;
import com.pj.squashrestapp.model.ScoreEventType;
import com.pj.squashrestapp.model.enums.ServePlayer;
import com.pj.squashrestapp.model.enums.ServeSide;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class JsonMatchScore {

    private Integer gameNumber;

    @JsonFormat(pattern = GeneralUtil.DATE_TIME_ISO_FORMAT)
    private ZonedDateTime zonedDateTime;

    private ScoreEventType scoreEventType;

    private AppealDecision appealDecision;

    private ServeSide serveSide;

    private ServePlayer servePlayer;

    private ServePlayer nextSuggestedServePlayer;

    private Integer firstPlayerScore;

    private Integer secondPlayerScore;

    private Integer firstPlayerGamesWon;

    private Integer secondPlayerGamesWon;

    private boolean canScore;

    private boolean canStartGame;

    private boolean canEndGame;

    private boolean canEndMatch;

    private boolean matchFinished;

}
