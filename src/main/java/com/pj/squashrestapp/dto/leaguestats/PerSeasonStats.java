package com.pj.squashrestapp.dto.leaguestats;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerSeasonStats {

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate seasonStartDate;

    private int seasonNumber;
    private String seasonNumberRoman;
    private UUID seasonUuid;
    private int rounds;
    private int regularMatches;
    private int tieBreakMatches;
    private BigDecimal tieBreakMatchesPercents;
    private int points;
    private int players;
    private BigDecimal playersAverage;
    private BigDecimal playersAveragePerRound;
    private BigDecimal playersAveragePerGroup;
    private BigDecimal groupsAveragePerRound;

    @JsonIgnore
    private Multimap<UUID, UUID> playersAttendicesMap;
}
