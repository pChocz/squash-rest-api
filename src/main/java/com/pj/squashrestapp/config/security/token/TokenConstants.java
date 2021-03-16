package com.pj.squashrestapp.config.security.token;

/**
 * Several constants related to the token authentication.
 */
public final class TokenConstants {

  public static final String TOKEN_PREFIX = "Bearer ";

  public static final String HEADER_STRING = "Authorization";

  public static final String HEADER_REFRESH_STRING = "Refresh";

  public static final String EXPOSE_HEADER_STRING = "Access-Control-Expose-Headers";

  /** Verification token expiration time in days */
  public static final long VERIFICATION_TOKEN_EXPIRATION_TIME_DAYS = 1;

  /** Access token expiration time in days */
  public static final long ACCESS_TOKEN_EXPIRATION_TIME_DAYS = 7;

  /** Refresh token expiration time in days */
  public static final long REFRESH_TOKEN_EXPIRATION_TIME_DAYS = 30;

}
