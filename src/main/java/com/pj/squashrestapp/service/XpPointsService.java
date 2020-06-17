package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class XpPointsService {

  @Autowired
  private XpPointsRepository xpPointsRepository;

  public ArrayListMultimap<String, Integer> buildAllAsIntegerMultimap() {
    final List<XpPointsForRound> allPointsForRounds = xpPointsRepository.findAll();
    final List<XpPointsForPlace> allPoints = xpPointsRepository.fetchAll();
    final ArrayListMultimap<String, Integer> multimap = EntityGraphBuildUtil.reconstructXpPointsForRound(allPointsForRounds, allPoints);
    return multimap;
  }

  public ArrayListMultimap<String, Integer> buildForGivenSplitAsMultimap(final String split) {
    final List<Integer> list = xpPointsRepository.retrievePointsBySplit(split);
    final ArrayListMultimap<String, Integer> multimap = ArrayListMultimap.create();
    multimap.putAll(split, list);
    return multimap;
  }

  public List<Integer> buildForGivenSplit(final String split) {
    final List<Integer> list = xpPointsRepository.retrievePointsBySplit(split);
    return list;
  }

  public List<XpPointsForRound> buildAllAsNativeObject() {
    final List<XpPointsForRound> allPointsForRounds = xpPointsRepository.findAll();
    final List<XpPointsForPlace> allPoints = xpPointsRepository.fetchAll();
    final List<XpPointsForRound> xpPointsForRound = EntityGraphBuildUtil.reconstructXpPointsList(allPointsForRounds, allPoints);
    return xpPointsForRound;
  }

}
