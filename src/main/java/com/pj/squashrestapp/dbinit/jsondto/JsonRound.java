package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class JsonRound {

  private int number;
  private String date;
  private ArrayList<JsonRoundGroup> groups;

}
