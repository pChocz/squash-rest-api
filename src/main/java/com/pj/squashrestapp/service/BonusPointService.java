package com.pj.squashrestapp.service;

import com.pj.squashrestapp.dto.BonusPointsAggregatedForLeague;
import com.pj.squashrestapp.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class BonusPointService {

    private final PlayerRepository playerRepository;
    private final SeasonRepository seasonRepository;
    private final BonusPointRepository bonusPointRepository;
    private final RedisCacheService redisCacheService;

    public List<BonusPoint> extractBonusPoints(final UUID seasonUuid) {
        final List<BonusPoint> bonusPoints = bonusPointRepository.findBySeasonUuid(seasonUuid);
        return bonusPoints;
    }

    public BonusPointsAggregatedForSeason extractBonusPointsAggregatedForSeason(final UUID seasonUuid) {
        final List<BonusPoint> bonusPoints = bonusPointRepository.findBySeasonUuid(seasonUuid);
        final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason =
                new BonusPointsAggregatedForSeason(seasonUuid, bonusPoints);
        return bonusPointsAggregatedForSeason;
    }

    public BonusPointsAggregatedForLeague extractBonusPointsAggregatedForLeague(final UUID leagueUuid) {
        final List<BonusPoint> bonusPoints = bonusPointRepository.findByLeagueUuid(leagueUuid);
        final BonusPointsAggregatedForLeague bonusPointsAggregatedForLeague =
                new BonusPointsAggregatedForLeague(leagueUuid, bonusPoints);
        return bonusPointsAggregatedForLeague;
    }

    @Transactional
    public BonusPoint applyBonusPointsForTwoPlayers(
            final UUID winnerUuid,
            final UUID looserUuid,
            final UUID seasonUuid,
            final LocalDate date,
            final int points) {
        final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
        final Player winner = playerRepository.findByUuid(winnerUuid);
        final Player looser = playerRepository.findByUuid(looserUuid);

        final BonusPoint bonusPoint = new BonusPoint();
        bonusPoint.setWinner(winner);
        bonusPoint.setLooser(looser);
        bonusPoint.setDate(date);
        bonusPoint.setPoints(points);

        season.addBonusPoint(bonusPoint);
        bonusPointRepository.save(bonusPoint);

        redisCacheService.evictCacheForBonusPoint(bonusPoint);
        log.info("Adding: {}", bonusPoint);
        return bonusPoint;
    }

    @Transactional
    public void deleteBonusPoint(final UUID uuid) {
        final BonusPoint bonusPoint = bonusPointRepository.findByUuid(uuid).orElseThrow();
        log.info("Removing: {}", bonusPoint);
        redisCacheService.evictCacheForBonusPoint(bonusPoint);
        bonusPointRepository.delete(bonusPoint);
    }
}
