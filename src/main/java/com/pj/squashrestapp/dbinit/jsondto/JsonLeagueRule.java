package com.pj.squashrestapp.dbinit.jsondto;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonLeagueRule {

  private UUID uuid;
  private String rule;
  private Double orderValue;

}
