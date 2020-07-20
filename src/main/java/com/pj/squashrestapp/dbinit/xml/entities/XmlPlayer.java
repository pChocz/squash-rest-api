package com.pj.squashrestapp.dbinit.xml.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "player")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class XmlPlayer {

  @Element
  @EqualsAndHashCode.Include
  private String name;

}
