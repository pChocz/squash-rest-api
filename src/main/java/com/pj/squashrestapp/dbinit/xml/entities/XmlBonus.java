package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "bonus")
@Data
@NoArgsConstructor
public class XmlBonus {

  @Element
  private String playerName;

  @Element
  private int points;

}
