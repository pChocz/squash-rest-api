package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonSeason {

    private int number;

    private UUID uuid;

    private String xpPointsType;

    private String description;

    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate startDate;

    private MatchFormatType matchFormatType;

    private SetWinningType regularSetWinningType;

    private SetWinningType tiebreakWinningType;

    private int regularSetWinningPoints;

    private int tiebreakWinningPoints;

    private int numberOfRounds;

    private int roundsToBeDeducted;

    private ArrayList<JsonBonusPoint> bonusPoints;

    private ArrayList<JsonLostBall> lostBalls;

    private ArrayList<JsonRound> rounds;
}
