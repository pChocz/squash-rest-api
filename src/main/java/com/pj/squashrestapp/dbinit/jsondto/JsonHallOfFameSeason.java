package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonHallOfFameSeason {

  @JsonProperty("sn")
  @SerializedName("sn")
  private int seasonNumber;

  @JsonProperty("l1")
  @SerializedName("l1")
  private String league1stPlace;

  @JsonProperty("l2")
  @SerializedName("l2")
  private String league2ndPlace;

  @JsonProperty("l3")
  @SerializedName("l3")
  private String league3rdPlace;

  @JsonProperty("c1")
  @SerializedName("c1")
  private String cup1stPlace;

  @JsonProperty("c2")
  @SerializedName("c2")
  private String cup2ndPlace;

  @JsonProperty("c3")
  @SerializedName("c3")
  private String cup3rdPlace;

  @JsonProperty("sc")
  @SerializedName("sc")
  private String superCupWinner;

  @JsonProperty("pc")
  @SerializedName("pc")
  private String pretendersCupWinner;

}
