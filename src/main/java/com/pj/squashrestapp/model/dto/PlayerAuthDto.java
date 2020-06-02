package com.pj.squashrestapp.model.dto;

import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PlayerAuthDto {

  String username;
  String password;
  AuthorityType authorityType;
  LeagueRole role;
  String leagueName;

}
