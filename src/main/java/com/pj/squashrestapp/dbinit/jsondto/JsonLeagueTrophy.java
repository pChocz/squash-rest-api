package com.pj.squashrestapp.dbinit.jsondto;

import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.model.audit.Audit;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class JsonLeagueTrophy {
    private int seasonNumber;
    private UUID playerUuid;
    private Trophy trophy;
    private Audit audit;
}
