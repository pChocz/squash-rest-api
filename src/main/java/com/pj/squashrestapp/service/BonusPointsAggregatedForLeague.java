package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.BonusPoint;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class BonusPointsAggregatedForLeague {

  final Long leagueId;
  final List<BonusPointsAggregatedForSeason> bonusPointsAggregatedForSeasons;

  public BonusPointsAggregatedForLeague(final Long leagueId, final List<BonusPoint> bonusPoints) {
    this.leagueId = leagueId;
    this.bonusPointsAggregatedForSeasons = new ArrayList<>();

    final Set<Long> allSeasonsIds = bonusPoints
            .stream()
            .map(bonusPoint -> bonusPoint
                    .getSeason()
                    .getId())
            .collect(Collectors.toSet());

    for (final Long seasonId : allSeasonsIds) {
      final List<BonusPoint> bonusPointsPerSeason = bonusPoints
              .stream()
              .filter(bonusPoint -> bonusPoint
                      .getSeason()
                      .getId()
                      .equals(seasonId))
              .collect(Collectors.toList());

      this.bonusPointsAggregatedForSeasons.add(new BonusPointsAggregatedForSeason(seasonId, bonusPointsPerSeason));
    }
  }

  public BonusPointsAggregatedForSeason forSeason(final Long seasonId) {
    return bonusPointsAggregatedForSeasons
            .stream()
            .filter(bonusPointsAggregatedForSeason -> bonusPointsAggregatedForSeason
                    .getSeasonId()
                    .equals(seasonId))
            .findFirst()
            .orElse(new BonusPointsAggregatedForSeason(seasonId, new ArrayList<>()));
  }

}
