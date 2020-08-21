package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class JsonMatch {

  private String firstPlayer;
  private String secondPlayer;
  private ArrayList<JsonSetResult> sets;

}
