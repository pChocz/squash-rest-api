package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Data
@NoArgsConstructor
public class JsonPlayerCredentials {

  private String username;
  private String passwordHashed;
  private String email;
  private String uuid;
  private String passwordSessionUuid;

}
