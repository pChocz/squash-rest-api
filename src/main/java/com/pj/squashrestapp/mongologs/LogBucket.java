package com.pj.squashrestapp.mongologs;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class LogBucket {
  private Date id;
  private Integer countSum;
}
