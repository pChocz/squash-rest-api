package com.pj.squashrestapp.dto.leaguestats;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OveralStats {

    private String leagueName;
    private UUID leagueUuid;
    private String time;
    private String location;
    private int seasons;
    private int players;
    private BigDecimal averagePlayersPerRound;
    private BigDecimal averagePlayersPerGroup;
    private BigDecimal averageGroupsPerRound;
    private int rounds;
    private int matches;
    private int sets;
    private int points;
    private MatchFormatType matchFormatType;
    private SetWinningType regularSetWinningType;
    private SetWinningType tiebreakWinningType;
    private int regularSetWinningPoints;
    private int tiebreakWinningPoints;
    private int numberOfRoundsPerSeason;
    private int roundsToBeDeducted;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate dateOfCreation;
}
