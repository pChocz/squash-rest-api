package com.pj.squashrestapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
public class XpPointsForTable {

  private final String split;
  private final int numberOfPlayers;
  private final List<XpPointsDto> xpPointsDtoList;

  public XpPointsForTable(final String split, final int numberOfPlayers) {
    this.split = split;
    this.numberOfPlayers = numberOfPlayers;
    this.xpPointsDtoList = new ArrayList<>();
  }

  public void addPoints(XpPointsDto xpPointsDto) {
    this.xpPointsDtoList.add(xpPointsDto);
  }

}
