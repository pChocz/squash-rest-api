package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(name = "round")
@Data
@NoArgsConstructor
public class XmlRound {

  @Element
  private int number;

  @Element
  private String date;

  @ElementList
  private ArrayList<XmlGroup> groups;

}
