package com.pj.squashrestapp.dto.leaguestats;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;
import com.pj.squashrestapp.util.GeneralUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/** */
@Getter
@Builder
public class PerSeasonStats {

  private final int seasonNumber;
  private final String seasonNumberRoman;

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private final LocalDate seasonStartDate;

  private final UUID seasonUuid;
  private final int rounds;
  private final int regularMatches;
  private final int tieBreakMatches;
  private final BigDecimal tieBreakMatchesPercents;
  private final int points;
  private final int players;
  private final BigDecimal playersAverage;

  @JsonIgnore private final Multimap<UUID, UUID> playersAttendicesMap;
}
