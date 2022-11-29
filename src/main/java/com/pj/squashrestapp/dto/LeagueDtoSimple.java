package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.UUID;

/** */
@Slf4j
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class LeagueDtoSimple {

    @EqualsAndHashCode.Include
    private UUID leagueUuid;

    private String leagueName;
    private MatchFormatType matchFormatType;
    private String name;
    private String location;
    private String time;
    private SetWinningType regularSetWinningType;
    private SetWinningType tiebreakWinningType;
    private int regularSetWinningPoints;
    private int tiebreakWinningPoints;
    private int numberOfRoundsPerSeason;
    private int roundsToBeDeducted;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private LocalDateTime dateOfCreation;

    public LeagueDtoSimple(final League league) {
        this.leagueUuid = league.getUuid();
        this.leagueName = league.getName();
        this.matchFormatType = league.getMatchFormatType();
        this.dateOfCreation = league.getDateOfCreation();
        this.name = league.getName();
        this.location = league.getLocation();
        this.time = league.getTime();
        this.regularSetWinningType = league.getRegularSetWinningType();
        this.tiebreakWinningType = league.getTiebreakWinningType();
        this.regularSetWinningPoints = league.getRegularSetWinningPoints();
        this.tiebreakWinningPoints = league.getTiebreakWinningPoints();
        this.numberOfRoundsPerSeason = league.getNumberOfRoundsPerSeason();
        this.roundsToBeDeducted = league.getRoundsToBeDeducted();
    }

    @Override
    public String toString() {
        return leagueName;
    }
}
