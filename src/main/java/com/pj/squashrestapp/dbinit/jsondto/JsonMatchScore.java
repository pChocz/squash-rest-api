package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.squashrestapp.model.AppealDecision;
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.ScoreEventType;
import com.pj.squashrestapp.model.ServePlayer;
import com.pj.squashrestapp.model.ServeSide;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

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
