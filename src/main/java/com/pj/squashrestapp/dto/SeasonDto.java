package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.util.GeneralUtil;
import com.pj.squashrestapp.util.RomanUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.UUID;

/** */
@Slf4j
@Getter
@NoArgsConstructor
public class SeasonDto implements Comparable<SeasonDto> {

    private UUID leagueUuid;
    private String leagueName;
    private UUID seasonUuid;
    private int seasonNumber;
    private String description;
    private String seasonNumberRoman;
    private String xpPointsType;
    private int allRounds;
    private int countedRounds;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate seasonStartDate;

    public SeasonDto(final Season season) {
        this.leagueUuid = season.getLeague().getUuid();
        this.leagueName = season.getLeague().getName();
        this.seasonUuid = season.getUuid();
        this.seasonNumber = season.getNumber();
        this.description = season.getDescription();
        this.seasonNumberRoman = RomanUtil.toRoman(season.getNumber());
        this.xpPointsType = season.getXpPointsType();
        this.seasonStartDate = season.getStartDate();
        this.allRounds = season.getNumberOfRounds();
        this.countedRounds = season.getNumberOfRounds() - season.getRoundsToBeDeducted();
    }

    @Override
    public int compareTo(final SeasonDto that) {
        return Comparator.comparingInt(SeasonDto::getSeasonNumber).compare(this, that);
    }

    @Override
    public String toString() {
        return "S: " + seasonNumber + " | " + leagueName + " | uuid: " + seasonUuid;
    }
}
