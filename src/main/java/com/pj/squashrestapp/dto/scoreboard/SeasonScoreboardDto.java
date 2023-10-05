package com.pj.squashrestapp.dto.scoreboard;

import com.pj.squashrestapp.dto.RoundDto;
import com.pj.squashrestapp.dto.SeasonDto;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.audit.Audit;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Getter
@NoArgsConstructor
public class SeasonScoreboardDto {

    private SeasonDto season;
    private int allRounds;
    private int finishedRounds;
    private int countedRounds;
    private int countedRoundsOnSeasonFinished;
    private List<SeasonScoreboardRowDto> seasonScoreboardRows;
    private List<RoundDto> rounds;
    private String xpPointsType;
    private Map<UUID, SeasonStar> seasonStars;
    private Audit audit;

    public SeasonScoreboardDto(final Season season) {
        this.season = new SeasonDto(season);
        this.seasonScoreboardRows = new ArrayList<>();
        this.xpPointsType = season.getXpPointsType();
        this.finishedRounds =
                (int) season.getRounds().stream().filter(Round::isFinished).count();
        this.allRounds = season.getNumberOfRounds();
        this.countedRoundsOnSeasonFinished = season.getNumberOfRounds() - season.getRoundsToBeDeducted();
        this.countedRounds = getNumberOfRoundsThatCount();
        this.rounds = season.getRounds().stream().map(RoundDto::new).collect(Collectors.toList());
        this.seasonStars = new HashMap<>();
        this.audit = season.getAudit();
    }

    private int getNumberOfRoundsThatCount() {
        if (finishedRounds == 0) {
            return 0;
        }
        if (finishedRounds == 1) {
            return 1;
        }
        return finishedRounds * countedRoundsOnSeasonFinished / allRounds;
    }

    public void sortByCountedPoints() {
        if (finishedRounds == allRounds) {
            seasonScoreboardRows.sort(Comparator.comparingInt(SeasonScoreboardRowDto::getCountedPoints)
                    .thenComparingInt(SeasonScoreboardRowDto::getTotalPoints)
                    .thenComparingDouble(SeasonScoreboardRowDto::getAverageAsDouble)
                    .reversed());
        } else {
            seasonScoreboardRows.sort(Comparator.comparingInt(SeasonScoreboardRowDto::getTotalPoints)
                    .thenComparingInt(SeasonScoreboardRowDto::getCountedPoints)
                    .thenComparingDouble(SeasonScoreboardRowDto::getAverageAsDouble)
                    .reversed());
        }
    }

    public void sortByTotalPoints() {
        seasonScoreboardRows.sort(
                Comparator.comparing(SeasonScoreboardRowDto::getTotalPoints).reversed());
    }

    @Override
    public String toString() {
        return "Season Scoreboard - S: "
                + this.getSeason().getSeasonNumber()
                + " | "
                + this.getSeason().getLeagueName();
    }
}
