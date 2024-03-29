package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonRound {
    private UUID uuid;
    private int number;
    private boolean finished;
    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    private LocalDate date;
    private ArrayList<JsonRoundGroup> groups;
    private Audit audit;
}
