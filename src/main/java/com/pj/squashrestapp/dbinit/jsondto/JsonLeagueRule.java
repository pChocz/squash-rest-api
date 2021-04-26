package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonLeagueRule {

  private UUID uuid;
  private String rule;
  private Double orderValue;

}
