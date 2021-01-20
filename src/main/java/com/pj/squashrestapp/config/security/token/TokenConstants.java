package com.pj.squashrestapp.config.security.token;

/**
 * Several constants related to the token authentication.
 */
public final class TokenConstants {

  public static final String TOKEN_PREFIX = "Bearer ";

  public static final String HEADER_STRING = "Authorization";

  public static final String EXPOSE_HEADER_STRING = "Access-Control-Expose-Headers";

  public static final String EXPIRATION_PREFIX = "exp";

  /** Expiration time in miliseconds */
  static final long TOKEN_EXPIRATION_TIME =
          2 * // months
          31 * // days
          24 * // hours
          60 * // minutes
          60 * // seconds
          1000L; // milliseconds

}
