package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

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
  private List<XmlHallOfFameSeason> hallOfFameSeasons;

  @ElementList
  private List<XmlSeason> seasons;

}
