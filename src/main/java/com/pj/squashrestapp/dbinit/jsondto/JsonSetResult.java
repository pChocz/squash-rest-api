package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonSetResult {

  @JsonProperty("p1")
  @SerializedName("p1")
  private Integer firstPlayerResult;

  @JsonProperty("p2")
  @SerializedName("p2")
  private Integer secondPlayerResult;
}
