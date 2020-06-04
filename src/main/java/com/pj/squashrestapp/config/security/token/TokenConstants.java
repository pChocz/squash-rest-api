package com.pj.squashrestapp.config.security.token;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * Several constants related to the token authentication.
 */
final class TokenConstants {

  /**
   * Expiration time in miliseconds.
   * <pre>
   * 86_400_000‬ ms = 1 day
   *  3_600_000 ms = 1 hour
   *     60_000 ms = 1 minute
   * </pre>
   * */
  static final long EXPIRATION_TIME = 860_400_000; // 10 days

  static final String TOKEN_PREFIX = "Bearer ";

  static final String HEADER_STRING = "Authorization";

}
