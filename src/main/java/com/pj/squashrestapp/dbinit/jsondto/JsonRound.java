package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonRound {

  private UUID uuid;
  private int number;
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate date;
  private ArrayList<JsonRoundGroup> groups;

}
