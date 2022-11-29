package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.enums.AdditionalMatchType;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import com.pj.squashrestapp.model.enums.SetWinningType;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonAdditionalMatch {

    private UUID uuid;

    private UUID firstPlayerUuid;

    private UUID secondPlayerUuid;

    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate date;

    private AdditionalMatchType type;

    private int seasonNumber;

    private MatchFormatType matchFormatType;

    private SetWinningType regularSetWinningType;

    private SetWinningType tiebreakWinningType;

    private int regularSetWinningPoints;

    private int tiebreakWinningPoints;

    private String footageLink;

    private ArrayList<JsonSetResult> sets;
}
