package com.pj.squashrestapp.dbinit.jsondto;

import com.pj.squashrestapp.dto.Trophy;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonLeagueTrophy {

  private int seasonNumber;

  private UUID playerUuid;

  private Trophy trophy;

}
