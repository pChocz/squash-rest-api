package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class JsonMatch {

  @JsonProperty("p1")
  @SerializedName("p1")
  private String firstPlayer;

  @JsonProperty("p2")
  @SerializedName("p2")
  private String secondPlayer;

  private ArrayList<JsonSetResult> sets;

}
