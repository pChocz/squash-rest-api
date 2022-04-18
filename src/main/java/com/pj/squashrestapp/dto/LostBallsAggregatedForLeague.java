package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.LostBall;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Getter
public final class LostBallsAggregatedForLeague {

    final UUID leagueUuid;
    final List<LostBallsAggregatedForSeason> lostBallsAggregatedForSeasons;

    public LostBallsAggregatedForLeague(final UUID leagueUuid, final List<LostBall> lostBalls) {
        this.leagueUuid = leagueUuid;
        this.lostBallsAggregatedForSeasons = new ArrayList<>();

        final Set<UUID> allSeasonsUuids = lostBalls.stream()
                .map(bonusPoint -> bonusPoint.getSeason().getUuid())
                .collect(Collectors.toSet());

        for (final UUID seasonUuid : allSeasonsUuids) {
            final List<LostBall> lostBallsPerSeason = lostBalls.stream()
                    .filter(lostBall -> lostBall.getSeason().getUuid().equals(seasonUuid))
                    .collect(Collectors.toList());

            this.lostBallsAggregatedForSeasons.add(new LostBallsAggregatedForSeason(seasonUuid, lostBallsPerSeason));
        }
    }

    public LostBallsAggregatedForSeason forSeason(final UUID seasonUuid) {
        return lostBallsAggregatedForSeasons.stream()
                .filter(bonusPointsAggregatedForSeason ->
                        bonusPointsAggregatedForSeason.getSeasonUuid().equals(seasonUuid))
                .findFirst()
                .orElse(new LostBallsAggregatedForSeason(seasonUuid, new ArrayList<>()));
    }
}
