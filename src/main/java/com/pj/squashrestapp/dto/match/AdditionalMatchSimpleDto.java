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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** */
@Getter
public class AdditionalMatchSimpleDto implements MatchDto {

    private final PlayerDto firstPlayer;
    private final PlayerDto secondPlayer;
    private final PlayerDto winner;
    private final AdditionalMatchType type;
    private final List<SetDto> sets;
    private final MatchStatus status;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private final LocalDate date;

    @JsonIgnore
    private final MatchFormatType matchFormatType;

    @JsonIgnore
    private final SetWinningType regularSetWinningType;

    @JsonIgnore
    private final int regularSetWinningPoints;

    @JsonIgnore
    private final SetWinningType tieBreakWinningType;

    @JsonIgnore
    private final int tieBreakWinningPoints;

    public AdditionalMatchSimpleDto(final AdditionalMatch match) {
        this.firstPlayer = new PlayerDto(match.getFirstPlayer());
        this.secondPlayer = new PlayerDto(match.getSecondPlayer());
        this.date = match.getDate();
        this.type = match.getType();
        this.matchFormatType = match.getMatchFormatType();
        this.regularSetWinningType = match.getRegularSetWinningType();
        this.regularSetWinningPoints = match.getRegularSetWinningPoints();
        this.tieBreakWinningType = match.getTiebreakWinningType();
        this.tieBreakWinningPoints = match.getTiebreakWinningPoints();

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
        return firstPlayer + " vs. " + secondPlayer + " : " + sets;
    }
}
