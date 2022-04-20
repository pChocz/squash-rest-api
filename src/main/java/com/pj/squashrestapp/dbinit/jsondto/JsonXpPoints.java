package com.pj.squashrestapp.dbinit.jsondto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** */
@Data
@NoArgsConstructor
public class JsonXpPoints {

    private List<JsonXpPointsForRound> xpPointsForRound;
}
