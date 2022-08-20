package com.pj.squashrestapp.dto.match;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatus;
import com.pj.squashrestapp.dto.matchresulthelper.MatchStatusHelper;
import com.pj.squashrestapp.model.AdditionalMatch;
import com.pj.squashrestapp.model.AdditionalMatchType;
import com.pj.squashrestapp.model.AdditionalSetResult;
import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** */
@Getter
@NoArgsConstructor
public class AdditionalMatchDetailedDto implements MatchDto {

    private UUID matchUuid;
    private PlayerDto firstPlayer;
    private PlayerDto secondPlayer;
    private PlayerDto winner;
    private UUID leagueUuid;
    private String leagueName;
    private AdditionalMatchType type;
    private int seasonNumber;
    private String footageLink;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate date;

    private List<SetDto> sets;

    @JsonIgnore
    private MatchFormatType matchFormatType;

    @JsonIgnore
    private SetWinningType regularSetWinningType;

    @JsonIgnore
    private int regularSetWinningPoints;

    @JsonIgnore
    private SetWinningType tieBreakWinningType;

    @JsonIgnore
    private int tieBreakWinningPoints;

    private MatchStatus status;

    public AdditionalMatchDetailedDto(final AdditionalMatch match) {
        this.matchUuid = match.getUuid();
        this.firstPlayer = new PlayerDto(match.getFirstPlayer());
        this.secondPlayer = new PlayerDto(match.getSecondPlayer());
        this.leagueUuid = match.getLeague().getUuid();
        this.leagueName = match.getLeague().getName();
        this.date = match.getDate();
        this.type = match.getType();
        this.seasonNumber = match.getSeasonNumber();
        this.matchFormatType = match.getMatchFormatType();
        this.regularSetWinningType = match.getRegularSetWinningType();
        this.regularSetWinningPoints = match.getRegularSetWinningPoints();
        this.tieBreakWinningType = match.getTiebreakWinningType();
        this.tieBreakWinningPoints = match.getTiebreakWinningPoints();
        this.footageLink = match.getFootageLink();

        this.sets = new ArrayList<>();
        for (final AdditionalSetResult setResult : match.getSetResults()) {
            this.sets.add(new SetDto(setResult, matchFormatType));
        }
        this.sets.sort(Comparator.comparingInt(SetDto::getSetNumber));

        this.status = MatchStatusHelper.checkStatus(this);
        this.winner = status == MatchStatus.FINISHED ? MatchStatusHelper.getWinner(this) : null;
    }

    @Override
    public boolean checkFinished() {
        return this.getStatus() == MatchStatus.FINISHED;
    }

    @Override
    public String toString() {
        return "[" + matchUuid + "] " + firstPlayer + " vs. " + secondPlayer + " : " + sets;
    }
}
