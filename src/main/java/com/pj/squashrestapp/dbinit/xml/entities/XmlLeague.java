package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "league")
@Data
@NoArgsConstructor
public class XmlLeague {

  @Element
  private String name;

  @Element
  private String logoBase64;

  @ElementList
  private ArrayList<XmlHallOfFameSeason> hallOfFameSeasons;

  @ElementList
  private ArrayList<XmlSeason> seasons;

}
