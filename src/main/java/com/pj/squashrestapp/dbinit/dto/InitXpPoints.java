package com.pj.squashrestapp.dbinit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 *
 */
@Getter
@NoArgsConstructor
@Root(name = "xpPoints")
public class InitXpPoints {

  @ElementList(inline = true, entry = "xpPointsForRound")
  private List<XpPointsForRoundDto> xpPointsForRound;

}
