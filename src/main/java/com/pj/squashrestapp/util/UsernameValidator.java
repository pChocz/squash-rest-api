package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class UsernameValidator {

  private final int MIN_LENGTH = 5;
  private final int MAX_LENGTH = 20;
  private final String MIN_MAX_CHAR = "{" + MIN_LENGTH + "," + MAX_LENGTH + "}";

  private final String ALL_LETTERS = "\\p{L}";
  private final String NUMBERS = "0-9";

  @SuppressWarnings("RegExpRedundantEscape")
  private final String OTHER_ALLOWED = "\\_\\-";

  private final String SPACE = "\\s";

  private final String PATTERN =
      "^[" + ALL_LETTERS + NUMBERS + OTHER_ALLOWED + SPACE + "]" + MIN_MAX_CHAR + "$";

  public boolean isValid(final String username) {
    return username.matches(PATTERN);
  }
}
