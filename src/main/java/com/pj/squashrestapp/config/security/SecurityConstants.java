package com.pj.squashrestapp.config.security;

/**
 * Several constants related to the Token authentication.
 */
@SuppressWarnings("JavaDoc")
public final class SecurityConstants {

  /**
   * Expiration time in miliseconds.
   * <pre>
   * 86_400_000‬ ms = 1 day
   *  3_600_000 ms = 1 hour
   *     60_000 ms = 1 minute
   * </pre>
   * */
  public static final long EXPIRATION_TIME = 3_600_000;

  public static final String SECRET = "SecretKeyToGenJWTs";

  public static final String TOKEN_PREFIX = "Bearer ";

  public static final String HEADER_STRING = "Authorization";

  public static final String SIGN_UP_URL = "/users/sign-up";

}
