package com.pj.squashrestapp.service;

import lombok.Value;

import java.util.UUID;

/**
 *
 */
@Value
public class TokenPair {

  String jwtAccessToken;
  UUID refreshToken;

}
