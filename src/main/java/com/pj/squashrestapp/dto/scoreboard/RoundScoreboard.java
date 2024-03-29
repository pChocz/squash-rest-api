package com.pj.squashrestapp.dto.scoreboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.MatchExtractorUtil;
import com.pj.squashrestapp.util.RomanUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** */
@Getter
@NoArgsConstructor
public class RoundScoreboard {

    private String leagueName;
    private UUID leagueUuid;

    private UUID seasonUuid;
    private int seasonNumber;
    private String seasonNumberRoman;
    private String split;

    private UUID roundUuid;
    private int roundNumber;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate roundDate;

    @JsonIgnore
    private List<Integer> playersPerGroup;

    private List<RoundGroupScoreboard> roundGroupScoreboards;
    private boolean finishedState;

    private int numberOfAllMatches;
    private int numberOfFinishedMatches;

    private Audit audit;

    public RoundScoreboard(final Round round) {
        this.leagueName = round.getSeason().getLeague().getName();
        this.leagueUuid = round.getSeason().getLeague().getUuid();
        this.seasonUuid = round.getSeason().getUuid();
        this.seasonNumber = round.getSeason().getNumber();
        this.seasonNumberRoman = RomanUtil.toRoman(this.seasonNumber);
        this.split = round.getSplit();
        this.roundUuid = round.getUuid();
        this.roundNumber = round.getNumber();
        this.roundDate = round.getDate();
        this.roundGroupScoreboards = new ArrayList<>();
        this.playersPerGroup = new ArrayList<>();
        this.finishedState = round.isFinished();
        for (final RoundGroup roundGroup : round.getRoundGroupsOrdered()) {
            this.addRoundGroupNew(roundGroup);
        }
        this.audit = round.getAudit();
    }

    private void addRoundGroupNew(final RoundGroup roundGroup) {
        final List<MatchDetailedDto> matches = MatchExtractorUtil.extractAllMatches(roundGroup);
        final RoundGroupScoreboard scoreboard = new RoundGroupScoreboard(matches);
        roundGroupScoreboards.add(scoreboard);
        playersPerGroup.add(scoreboard.getScoreboardRows().size());
        numberOfAllMatches += matches.size();
    }

    public void assignPointsAndPlaces(final List<Integer> xpPoints) {
        int i = 0;
        for (final RoundGroupScoreboard scoreboard : roundGroupScoreboards) {
            int j = 1;
            for (final RoundGroupScoreboardRow scoreboardRow : scoreboard.getScoreboardRows()) {
                scoreboardRow.setPlaceInRound(i + 1);
                scoreboardRow.setPlaceInGroup(j++);
                scoreboardRow.setXpEarned(xpPoints.get(i++));
            }
            this.numberOfFinishedMatches += scoreboard.getMatches().stream()
                    .filter(MatchDetailedDto::checkFinished)
                    .count();
        }
    }

    @Override
    public String toString() {
        return "Round Scoreboard - R: " + roundNumber + " | S: " + seasonNumber + " | " + leagueName;
    }
}
