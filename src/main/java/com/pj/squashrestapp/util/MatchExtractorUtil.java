package com.pj.squashrestapp.util;

import com.pj.squashrestapp.dto.match.MatchDetailedDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class MatchExtractorUtil {

  public List<MatchDetailedDto> extractAllMatches(final League league) {
    return league.getSeasonsOrdered().stream()
        .flatMap(season -> season.getRoundsOrdered().stream())
        .flatMap(round -> round.getRoundGroupsOrdered().stream())
        .flatMap(roundGroup -> roundGroup.getMatches().stream())
        .map(MatchDetailedDto::new)
        .collect(Collectors.toList());
  }

  public List<MatchDetailedDto> extractAllMatches(final Season season) {
    return season.getRoundsOrdered().stream()
        .flatMap(round -> round.getRoundGroupsOrdered().stream())
        .flatMap(roundGroup -> roundGroup.getMatches().stream())
        .map(MatchDetailedDto::new)
        .collect(Collectors.toList());
  }

  public List<MatchDetailedDto> extractAllMatches(final Round round) {
    return round.getRoundGroupsOrdered().stream()
        .flatMap(roundGroup -> roundGroup.getMatches().stream())
        .map(MatchDetailedDto::new)
        .collect(Collectors.toList());
  }

  public List<MatchDetailedDto> extractAllMatches(final RoundGroup roundGroup) {
    return roundGroup.getMatches().stream().map(MatchDetailedDto::new).collect(Collectors.toList());
  }
}
