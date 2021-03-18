package com.pj.squashrestapp.service;

import com.google.common.collect.ArrayListMultimap;
import com.pj.squashrestapp.model.XpPointsForPlace;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.model.XpPointsForRoundGroup;
import com.pj.squashrestapp.dto.XpPointsDto;
import com.pj.squashrestapp.dto.XpPointsForTable;
import com.pj.squashrestapp.repository.XpPointsRepository;
import com.pj.squashrestapp.util.EntityGraphBuildUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XpPointsService {

  private final XpPointsRepository xpPointsRepository;

  public ArrayListMultimap<String, Integer> buildAllAsIntegerMultimap() {
    final List<XpPointsForRound> allPointsForRounds = xpPointsRepository.findAll();
    final List<XpPointsForPlace> allPoints = xpPointsRepository.fetchAll();
    final ArrayListMultimap<String, Integer> multimap = EntityGraphBuildUtil.reconstructXpPointsForRound(allPointsForRounds, allPoints);
    return multimap;
  }

  public List<XpPointsForTable> buildXpPointsForTableForType(final String type) {
    final List<XpPointsForRound> xpPointsForRoundList = buildAllAsNativeObjectForType(type);
    return build(xpPointsForRoundList);
  }

  private List<XpPointsForRound> buildAllAsNativeObjectForType(final String type) {
    final List<XpPointsForRound> allPointsForRounds = xpPointsRepository.findAllByType(type);
    final List<XpPointsForPlace> allPoints = xpPointsRepository.fetchAllByType(type);
    final List<XpPointsForRound> xpPointsForRound = EntityGraphBuildUtil.reconstructXpPointsList(allPointsForRounds, allPoints);
    return xpPointsForRound;
  }

  private List<XpPointsForTable> build(final List<XpPointsForRound> xpPointsForRoundList) {
    final List<XpPointsForTable> xpPointsForTableList = new ArrayList<>();

    for (final XpPointsForRound xpPointsForRound : xpPointsForRoundList) {
      final String type = xpPointsForRound.getType();
      final String split = xpPointsForRound.getSplit();
      final int numberOfPlayers = xpPointsForRound.getNumberOfPlayers();
      final XpPointsForTable xpPointsForTable = new XpPointsForTable(type, split, numberOfPlayers);

      for (final XpPointsForRoundGroup xpPointsForRoundGroup : xpPointsForRound.getXpPointsForRoundGroups()) {
        final int groupNumber = xpPointsForRoundGroup.getRoundGroupNumber();

        for (final XpPointsForPlace xpPointsForPlace : xpPointsForRoundGroup.getXpPointsForPlaces()) {
          final int placeInRound = xpPointsForPlace.getPlaceInRound();
          final int placeInGroup = xpPointsForPlace.getPlaceInRoundGroup();
          final int points = xpPointsForPlace.getPoints();

          final XpPointsDto xpPointsDto = new XpPointsDto(placeInRound, placeInGroup, groupNumber, points);
          xpPointsForTable.addPoints(xpPointsDto);
        }
      }

      xpPointsForTableList.add(xpPointsForTable);
    }

    return xpPointsForTableList;
  }

  public List<XpPointsForTable> buildXpPointsForTableAll() {
    final List<XpPointsForRound> xpPointsForRoundList = buildAllAsNativeObject();
    return build(xpPointsForRoundList);
  }

  public List<XpPointsForRound> buildAllAsNativeObject() {
    final List<XpPointsForRound> allPointsForRounds = xpPointsRepository.findAll();
    final List<XpPointsForPlace> allPoints = xpPointsRepository.fetchAll();
    final List<XpPointsForRound> xpPointsForRound = EntityGraphBuildUtil.reconstructXpPointsList(allPointsForRounds, allPoints);
    return xpPointsForRound;
  }

  public List<String> getTypes() {
    final List<String> allTypes = xpPointsRepository.getAllTypes();
    return allTypes;
  }

}
