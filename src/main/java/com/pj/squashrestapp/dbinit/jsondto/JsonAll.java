package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pj.squashrestapp.model.XpPointsForRound;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class JsonAll {

  private final List<JsonXpPointsForRound> xpPoints;
  private final List<JsonLeague> leagues;
  private final List<JsonPlayerCredentials> credentials;
  private final List<JsonRefreshToken> refreshTokens;
  private final List<JsonVerificationToken> verificationTokens;

}
