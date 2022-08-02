package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonHallOfFameSeason {

    private int seasonNumber;

    private UUID league1stPlace;

    private UUID league2ndPlace;

    private UUID league3rdPlace;

    private UUID cup1stPlace;

    private UUID cup2ndPlace;

    private UUID cup3rdPlace;

    private UUID superCupWinner;

    private UUID pretendersCupWinner;

    private List<UUID> allRoundsAttendees;

    private List<UUID> coviders;
}
