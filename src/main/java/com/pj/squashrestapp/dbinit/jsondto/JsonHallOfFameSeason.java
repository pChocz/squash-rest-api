package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;
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
  private UUID league1stPlace;

  @JsonProperty("l2")
  @SerializedName("l2")
  private UUID league2ndPlace;

  @JsonProperty("l3")
  @SerializedName("l3")
  private UUID league3rdPlace;

  @JsonProperty("c1")
  @SerializedName("c1")
  private UUID cup1stPlace;

  @JsonProperty("c2")
  @SerializedName("c2")
  private UUID cup2ndPlace;

  @JsonProperty("c3")
  @SerializedName("c3")
  private UUID cup3rdPlace;

  @JsonProperty("sc")
  @SerializedName("sc")
  private UUID superCupWinner;

  @JsonProperty("pc")
  @SerializedName("pc")
  private UUID pretendersCupWinner;

  @JsonProperty("all")
  @SerializedName("all")
  private List<UUID> allRoundsAttendees;

  @JsonProperty("cov")
  @SerializedName("cov")
  private List<UUID> coviders;

}
