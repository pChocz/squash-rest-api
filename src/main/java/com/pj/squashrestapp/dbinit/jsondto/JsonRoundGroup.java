package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class JsonRoundGroup {

  private int number;
  private ArrayList<JsonPlayer> players;
  private ArrayList<JsonMatch> matches;

}
