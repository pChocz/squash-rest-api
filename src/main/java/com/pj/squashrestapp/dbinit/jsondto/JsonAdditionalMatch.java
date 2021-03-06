package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.pj.squashrestapp.model.AdditionalMatchType;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonAdditionalMatch {

  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate date;

  private AdditionalMatchType type;

  private int seasonNumber;

  @JsonProperty("p1")
  @SerializedName("p1")
  private UUID firstPlayer;

  @JsonProperty("p2")
  @SerializedName("p2")
  private UUID secondPlayer;

  private ArrayList<JsonSetResult> sets;
}
