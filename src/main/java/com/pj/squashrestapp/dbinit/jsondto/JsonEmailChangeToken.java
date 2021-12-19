package com.pj.squashrestapp.dbinit.jsondto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pj.squashrestapp.util.GeneralUtil;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class JsonEmailChangeToken {

  @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
  private LocalDateTime expirationDateTime;

  private UUID playerUuid;
  private UUID token;
  private String newEmail;
}
