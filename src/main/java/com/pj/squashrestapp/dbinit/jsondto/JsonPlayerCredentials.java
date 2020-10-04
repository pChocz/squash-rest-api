package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class JsonPlayerCredentials {

  private String username;
  private String password;
  private String passwordHashed;
  private String email;
  private UUID uuid;
  private UUID passwordSessionUuid;
  private List<JsonLeagueRoles> leagueRoles;
  private List<JsonAuthorities> authorities;

}
