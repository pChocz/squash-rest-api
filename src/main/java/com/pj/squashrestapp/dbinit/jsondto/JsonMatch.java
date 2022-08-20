package com.pj.squashrestapp.dbinit.jsondto;

import com.pj.squashrestapp.model.MatchFormatType;
import com.pj.squashrestapp.model.SetWinningType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonMatch {

    private int number;

    private UUID firstPlayerUuid;

    private UUID secondPlayerUuid;

    private MatchFormatType matchFormatType;

    private SetWinningType regularSetWinningType;

    private SetWinningType tiebreakWinningType;

    private int regularSetWinningPoints;

    private int tiebreakWinningPoints;

    private String footageLink;
    private ArrayList<JsonSetResult> sets;
}
