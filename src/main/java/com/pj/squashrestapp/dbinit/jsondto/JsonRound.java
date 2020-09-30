package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class JsonRound {

  private int number;
  @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
  private LocalDate date;
  private ArrayList<JsonRoundGroup> groups;

}
