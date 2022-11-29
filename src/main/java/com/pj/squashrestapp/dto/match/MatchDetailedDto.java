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
import com.pj.squashrestapp.model.Match;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.MatchScore;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.SetResult;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** */
@Getter
@NoArgsConstructor
public class MatchDetailedDto implements MatchDto {

    private UUID matchUuid;
    private PlayerDto firstPlayer;
    private PlayerDto secondPlayer;
    private PlayerDto winner;
    private int roundGroupNumber;
    private UUID roundUuid;
    private int roundNumber;
    private UUID seasonUuid;
    private int seasonNumber;
    private String leagueName;
    private UUID leagueUuid;
    private List<SetDto> sets;
    private MatchStatus status;
    private String footageLink;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate date;

    private Audit audit;

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

    @Setter
    private List<MatchScore> matchScores;

    public MatchDetailedDto(final Match match) {
        final RoundGroup roundGroup = match.getRoundGroup();
        final Round round = roundGroup.getRound();
        final Season season = round.getSeason();

        this.matchUuid = match.getUuid();
        this.firstPlayer = new PlayerDto(match.getFirstPlayer());
        this.secondPlayer = new PlayerDto(match.getSecondPlayer());
        this.roundGroupNumber = roundGroup.getNumber();
        this.date = round.getDate();
        this.audit = match.getAudit();
        this.roundUuid = round.getUuid();
        this.roundNumber = round.getNumber();
        this.seasonUuid = season.getUuid();
        this.seasonNumber = season.getNumber();
        this.leagueName = season.getLeague().getName();
        this.leagueUuid = season.getLeague().getUuid();

        this.matchFormatType = match.getMatchFormatType();
        this.regularSetWinningType = match.getRegularSetWinningType();
        this.regularSetWinningPoints = match.getRegularSetWinningPoints();
        this.tieBreakWinningType = match.getTiebreakWinningType();
        this.tieBreakWinningPoints = match.getTiebreakWinningPoints();
        this.footageLink = match.getFootageLink();

        this.sets = new ArrayList<>();
        for (final SetResult setResult : match.getSetResults()) {
            this.sets.add(new SetDto(setResult, matchFormatType));
        }
        this.matchScores = match.getMatchScoresOrdered();
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
