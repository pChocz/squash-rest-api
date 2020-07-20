package com.pj.squashrestapp.dbinit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 */
@Getter
@Root(name = "player")
@NoArgsConstructor
public class InitPlayer {

  @Element
  private String username;

  @Element
  private String passwordHashed;

  @Element
  private String email;

  @Element
  private String uuid;

  @Element
  private String passwordSessionUuid;

}
