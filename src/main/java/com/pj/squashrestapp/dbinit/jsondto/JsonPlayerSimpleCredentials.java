package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class JsonPlayerSimpleCredentials {

  private String username;
  private String password;
  private String email;
  private List<JsonLeagueRoles> leagueRoles;
  private List<JsonAuthorities> authorities;

}
