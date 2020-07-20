package com.pj.squashrestapp.service;

import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.repository.BonusPointRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.SeasonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class BonusPointService {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private SeasonRepository seasonRepository;

  @Autowired
  private BonusPointRepository bonusPointRepository;

  public List<BonusPoint> extractBonusPoints(final Long playerId, final Long seasonId) {

    TESTextractBonusPointsForSeason();

    final List<BonusPoint> bonusPoints = bonusPointRepository.findByPlayerIdAndSeasonId(playerId, seasonId);
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

  public void TESTextractBonusPointsForSeason() {

//    final Long seasonId = 2L;
//    final List<BonusPoint> bonusPointsS = bonusPointRepository.findBySeasonId(seasonId);
//    final var bonusPointsAggregatedForSeason = new BonusPointsAggregatedForSeason(seasonId, bonusPointsS);

    final Long leagueId = 1L;
    final List<BonusPoint> bonusPointsL = bonusPointRepository.findByLeagueId(leagueId);
    final var bonusPointsAggregatedForLeague = new BonusPointsAggregatedForLeague(leagueId, bonusPointsL);

    final int p1 = bonusPointsAggregatedForLeague.forSeason(1L).forPlayer(2L);
    final int p2 = bonusPointsAggregatedForLeague.forSeason(1L).forPlayer(3L);
    final int p3 = bonusPointsAggregatedForLeague.forSeason(2L).forPlayer(3L);


    log.info("dupa");
  }

  public List<BonusPoint> applyPoints(final Long winnerId, final Long looserId, final Long seasonId, final int points) {
    final Season season = seasonRepository.findById(seasonId).get();
    final Player winner = playerRepository.findById(winnerId).get();
    final Player looser = playerRepository.findById(looserId).get();

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
