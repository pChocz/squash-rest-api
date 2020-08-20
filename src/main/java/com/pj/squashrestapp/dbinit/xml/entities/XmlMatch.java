package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "match")
@Data
@NoArgsConstructor
public class XmlMatch {

  @Element
  private String firstPlayer;

  @Element
  private String secondPlayer;

  @ElementList
  private ArrayList<XmlSet> sets;

}
