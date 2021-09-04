package com.pj.squashrestapp.dbinit.jsondto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private boolean enabled;
  private Boolean wantsEmails;
  private String locale;
  private List<JsonLeagueRoles> leagueRoles;
  private List<JsonAuthorities> authorities;
}
