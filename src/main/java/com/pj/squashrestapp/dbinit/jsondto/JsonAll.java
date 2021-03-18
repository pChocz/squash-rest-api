package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
