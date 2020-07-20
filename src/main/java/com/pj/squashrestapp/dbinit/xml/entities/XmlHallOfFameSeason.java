package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "hallOfFameSeason")
@Data
@NoArgsConstructor
public class XmlHallOfFameSeason {

  @Element
  private int seasonNumber;

  @Element
  private String league1stPlace;

  @Element
  private String league2ndPlace;

  @Element
  private String league3rdPlace;

  @Element
  private String cup1stPlace;

  @Element
  private String cup2ndPlace;

  @Element
  private String cup3rdPlace;

  @Element
  private String superCupWinner;

  @Element
  private String pretendersCupWinner;

}
