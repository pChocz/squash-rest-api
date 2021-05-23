package com.pj.squashrestapp.dbinit.jsondto;

import java.util.ArrayList;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonRoundGroup {

  private int number;
  private ArrayList<JsonPlayer> players;
  private ArrayList<JsonMatch> matches;

}
