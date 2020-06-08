package com.pj.squashrestapp.controller;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.Season;
import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphReconstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class XpPointsService {

  @Autowired
  private XpPointsRepository xpPointsRepository;

  public ArrayListMultimap<String, Integer> buildAll() {
    final List<XpPointsForRound> allPointsForRounds = xpPointsRepository.findAll();
    final List<XpPointsForPlace> allPoints = xpPointsRepository.fetchAll();
    final ArrayListMultimap<String, Integer> xpPointsForRound = EntityGraphReconstruct.reconstructXpPointsForRound(allPointsForRounds, allPoints);
    return xpPointsForRound;
  }

}
