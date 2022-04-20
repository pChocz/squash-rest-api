package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.BonusPoint;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Getter
public final class BonusPointsAggregatedForLeague {

    final UUID leagueUuid;
    final List<BonusPointsAggregatedForSeason> bonusPointsAggregatedForSeasons;

    public BonusPointsAggregatedForLeague(final UUID leagueUuid, final List<BonusPoint> bonusPoints) {
        this.leagueUuid = leagueUuid;
        this.bonusPointsAggregatedForSeasons = new ArrayList<>();

        final Set<UUID> allSeasonsUuids = bonusPoints.stream()
                .map(bonusPoint -> bonusPoint.getSeason().getUuid())
                .collect(Collectors.toSet());

        for (final UUID seasonUuid : allSeasonsUuids) {
            final List<BonusPoint> bonusPointsPerSeason = bonusPoints.stream()
                    .filter(bonusPoint -> bonusPoint.getSeason().getUuid().equals(seasonUuid))
                    .collect(Collectors.toList());

            this.bonusPointsAggregatedForSeasons.add(
                    new BonusPointsAggregatedForSeason(seasonUuid, bonusPointsPerSeason));
        }
    }

    public BonusPointsAggregatedForSeason forSeason(final UUID seasonUuid) {
        return bonusPointsAggregatedForSeasons.stream()
                .filter(bonusPointsAggregatedForSeason ->
                        bonusPointsAggregatedForSeason.getSeasonUuid().equals(seasonUuid))
                .findFirst()
                .orElse(new BonusPointsAggregatedForSeason(seasonUuid, new ArrayList<>()));
    }
}
