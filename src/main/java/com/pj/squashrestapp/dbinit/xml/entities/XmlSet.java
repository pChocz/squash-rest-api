package com.pj.squashrestapp.dbinit.xml.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "set")
@Data
@NoArgsConstructor
public class XmlSet {

  @Element
  private int firstPlayerResult;

  @Element
  private int secondPlayerResult;

}
