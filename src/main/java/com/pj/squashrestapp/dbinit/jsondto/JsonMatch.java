package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonMatch {

    @JsonProperty("p1")
    @SerializedName("p1")
    private UUID firstPlayer;

    @JsonProperty("p2")
    @SerializedName("p2")
    private UUID secondPlayer;

    private ArrayList<JsonSetResult> sets;
}
