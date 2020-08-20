package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "group")
@Data
@NoArgsConstructor
public class XmlGroup {

  @Element
  int id;

  @ElementList
  private ArrayList<XmlPlayer> players;

  @ElementList
  private ArrayList<XmlMatch> matches;

}
