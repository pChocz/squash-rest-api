package com.pj.squashrestapp.config.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * Several constants related to the token authentication.
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
  public static final long EXPIRATION_TIME = 860_400_000; // 10 days

  public static final String TOKEN_PREFIX = "Bearer ";

  public static final String HEADER_STRING = "Authorization";

  public static final String SIGN_UP_URL = "/users/sign-up";

  public static final Key SECRET_KEY =
          Keys.hmacShaKeyFor(
                  Decoders.BASE64.decode(
                          "QLTIh5c0pedaXdRzckPnlr1OCHuMcBmhHGzACijWJ0zlJ7Ua5LFkiRwX0e2TMIHo1tQLNHk6INSFc6JXY2g6letWJClMHYp2XO1f"));

}
