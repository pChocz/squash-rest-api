package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonLeague {

    private String name;

    private String time;

    private String location;

    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private LocalDateTime dateOfCreation;

    private String logoBase64;

    private UUID uuid;

    private MatchFormatType matchFormatType;

    private SetWinningType regularSetWinningType;

    private SetWinningType tiebreakWinningType;

    private int regularSetWinningPoints;

    private int tiebreakWinningPoints;

    private int numberOfRoundsPerSeason;

    private int roundsToBeDeducted;

    private ArrayList<JsonSeason> seasons;

    private ArrayList<JsonLeagueTrophy> trophies;

    private ArrayList<JsonLeagueRule> rules;

    private ArrayList<JsonAdditionalMatch> additionalMatches;
}
