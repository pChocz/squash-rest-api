package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonPlayerCredentials {

  private String username;
  private String password;
  private String email;
  private UUID uuid;
  private UUID passwordSessionUuid;
  private List<JsonLeagueRoles> leagueRoles;
  private List<JsonAuthorities> authorities;

}
