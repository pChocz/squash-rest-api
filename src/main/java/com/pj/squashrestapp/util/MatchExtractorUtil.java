package com.pj.squashrestapp.util;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Round;
import com.pj.squashrestapp.model.RoundGroup;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.MatchDto;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class MatchExtractorUtil {

  public List<MatchDto> extractAllMatches(final League league) {
    return league
            .getSeasonsOrdered()
            .stream()
            .flatMap(season -> season
                    .getRoundsOrdered()
                    .stream())
            .flatMap(round -> round
                    .getRoundGroupsOrdered()
                    .stream())
            .flatMap(roundGroup -> roundGroup
                    .getMatches()
                    .stream())
            .map(MatchDto::new)
            .collect(Collectors.toList());
  }

  public List<MatchDto> extractAllMatches(final Season season) {
    return season
            .getRoundsOrdered()
            .stream()
            .flatMap(round -> round
                    .getRoundGroupsOrdered()
                    .stream())
            .flatMap(roundGroup -> roundGroup
                    .getMatches()
                    .stream())
            .map(MatchDto::new)
            .collect(Collectors.toList());
  }


  public List<MatchDto> extractAllMatches(final Round round) {
    return round
            .getRoundGroupsOrdered()
            .stream()
            .flatMap(roundGroup -> roundGroup
                    .getMatches()
                    .stream())
            .map(MatchDto::new)
            .collect(Collectors.toList());
  }

  public List<MatchDto> extractAllMatches(final RoundGroup roundGroup) {
    return roundGroup
            .getMatches()
            .stream()
            .map(MatchDto::new)
            .collect(Collectors.toList());
  }

}
