package com.pj.squashrestapp.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedMultiset;
import com.pj.squashrestapp.model.dto.MatchDto;
import com.pj.squashrestapp.model.dto.SingleSetRowDto;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@UtilityClass
public class MatchUtil {

  public Multimap<Long, MatchDto> rebuildRoundMatchesPerRoundGroupId(final List<SingleSetRowDto> sets) {
    final List<MatchDto> matchesRebuilded = rebuildMatches(sets);

    final ListMultimap<Long, MatchDto> perGroupMatches = MultimapBuilder.treeKeys().arrayListValues().build();
    for (final MatchDto matchDto : matchesRebuilded) {
      perGroupMatches.put(matchDto.getRoundGroupId(), matchDto);
    }

    return perGroupMatches;
  }

  public List<MatchDto> rebuildMatches(final List<SingleSetRowDto> sets) {
    final Multimap<Long, SingleSetRowDto> multimap = ArrayListMultimap.create();

    for (final SingleSetRowDto singleSetRowDto : sets) {
      if (singleSetRowDto.hasBeenPlayed()) {
        multimap.put(singleSetRowDto.getMatchId(), singleSetRowDto);
      }
    }

    return multimap
            .keySet()
            .stream()
            .map(matchId -> new MatchDto(new ArrayList<>(multimap.get(matchId))))
            .collect(Collectors.toCollection(ArrayList::new));
  }

  public Multimap<Integer, MatchDto> rebuildRoundMatchesPerSeasonNumber(final List<SingleSetRowDto> sets) {
    final List<MatchDto> matchesRebuilded = rebuildMatches(sets);

    final Multimap<Integer, MatchDto> perSeasonMatches = ArrayListMultimap.create();
    for (final MatchDto matchDto : matchesRebuilded) {
      perSeasonMatches.put(matchDto.getSeasonNumber(), matchDto);
    }

    return perSeasonMatches;
  }

  /**
   * Converts list of Integer to nicely formatted String,
   * ex: 1 | 3 | 4
   *
   * @param integerList list of integers to format
   * @return nicely formatted String
   */
  public String integerListToString(final List<Integer> integerList) {
    return integerList
            .stream()
            .map(Object::toString)
            .collect(Collectors.joining(" | "));
  }

}
