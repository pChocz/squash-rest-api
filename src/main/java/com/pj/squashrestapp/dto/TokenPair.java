package com.pj.squashrestapp.dto;

import java.util.UUID;
import lombok.Value;

/** */
@Value
public class TokenPair {

  String jwtAccessToken;
  UUID refreshToken;
}
