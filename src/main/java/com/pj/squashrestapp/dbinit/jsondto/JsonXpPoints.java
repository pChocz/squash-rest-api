package com.pj.squashrestapp.dbinit.jsondto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
public class JsonXpPoints {

  private List<JsonXpPointsForRound> xpPointsForRound;

}
