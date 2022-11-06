package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
public class LeagueDtoSimple {

    @EqualsAndHashCode.Include
    private final UUID leagueUuid;

    private final String leagueName;
    private final MatchFormatType matchFormatType;
    private final String name;
    private final String location;
    private final String time;
    private final SetWinningType regularSetWinningType;
    private final SetWinningType tiebreakWinningType;
    private final int regularSetWinningPoints;
    private final int tiebreakWinningPoints;
    private final int numberOfRoundsPerSeason;
    private final int roundsToBeDeducted;
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private final LocalDateTime dateOfCreation;

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
