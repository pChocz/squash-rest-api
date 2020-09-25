package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.dto.BonusPointsAggregatedForLeague;
import com.pj.squashrestapp.model.dto.BonusPointsAggregatedForSeason;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BonusPointService {

  private final PlayerRepository playerRepository;
  private final SeasonRepository seasonRepository;
  private final BonusPointRepository bonusPointRepository;


  public List<BonusPoint> extractBonusPoints(final UUID playerUuid, final UUID seasonUuid) {
    final List<BonusPoint> bonusPoints = bonusPointRepository.findByPlayerUuidAndSeasonUuid(playerUuid, seasonUuid);
    return bonusPoints;
  }

  public BonusPointsAggregatedForSeason extractBonusPointsAggregatedForSeason(final Long seasonId) {
    final List<BonusPoint> bonusPoints = bonusPointRepository.findBySeasonId(seasonId);
    final BonusPointsAggregatedForSeason bonusPointsAggregatedForSeason = new BonusPointsAggregatedForSeason(seasonId, bonusPoints);
    return bonusPointsAggregatedForSeason;
  }

  public BonusPointsAggregatedForLeague extractBonusPointsAggregatedForLeague(final Long leagueId) {
    final List<BonusPoint> bonusPoints = bonusPointRepository.findByLeagueId(leagueId);
    final BonusPointsAggregatedForLeague bonusPointsAggregatedForLeague = new BonusPointsAggregatedForLeague(leagueId, bonusPoints);
    return bonusPointsAggregatedForLeague;
  }

  @Transactional
  public List<BonusPoint> applyBonusPointsForTwoPlayers(final UUID winnerUuid, final UUID looserUuid,
                                                        final UUID seasonUuid, final int points) {
    final Season season = seasonRepository.findSeasonByUuid(seasonUuid).orElseThrow();
    final Player winner = playerRepository.findByUuid(winnerUuid);
    final Player looser = playerRepository.findByUuid(looserUuid);

    final BonusPoint bonusPointForWinner = new BonusPoint();
    bonusPointForWinner.setPlayer(winner);
    bonusPointForWinner.setPoints(points);

    final BonusPoint bonusPointForLooser = new BonusPoint();
    bonusPointForLooser.setPlayer(looser);
    bonusPointForLooser.setPoints(-points);

    season.addBonusPoint(bonusPointForWinner);
    season.addBonusPoint(bonusPointForLooser);

    final List<BonusPoint> list = Arrays.asList(bonusPointForWinner, bonusPointForLooser);
    bonusPointRepository.saveAll(list);

    return list;
  }

}
