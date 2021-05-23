package com.pj.squashrestapp.dbinit.jsondto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
