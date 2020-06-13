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
    final List<BonusPoint> bonusPoints = bonusPointRepository.findByPlayerIdAndSeasonId(playerId, seasonId);
    return bonusPoints;
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
