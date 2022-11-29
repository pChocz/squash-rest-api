package com.pj.squashrestapp.dbinit.jsondto;

import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonMatch {
    private int number;
    private UUID uuid;
    private UUID firstPlayerUuid;
    private UUID secondPlayerUuid;
    private MatchFormatType matchFormatType;
    private SetWinningType regularSetWinningType;
    private SetWinningType tiebreakWinningType;
    private int regularSetWinningPoints;
    private int tiebreakWinningPoints;
    private String footageLink;
    private ArrayList<JsonSetResult> sets;
    private ArrayList<JsonMatchScore> matchScores;
    private Audit audit;
}
