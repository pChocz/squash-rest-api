package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "season")
@Data
@NoArgsConstructor
public class XmlSeason {

  @Element
  private int id;

  @Element
  private String startDate;

  @ElementList
  private List<XmlBonus> bonusPoints;

  @ElementList
  private List<XmlRound> rounds;

}
