package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;

/**
 *
 */
@UtilityClass
public class PasswordStrengthValidator {

  private final int MIN_LENGHT = 5;
  private final String MIN_MAX_CHAR = ".{" + MIN_LENGHT + ",}";

  private final String LOWER_CASE = "(?=.*[a-z])";
  private final String UPPER_CASE = "(?=.*[A-Z])";
  private final String NO_SPACE = "(?=\\S+$)";

  private final String PATTERN = LOWER_CASE + UPPER_CASE + NO_SPACE + MIN_MAX_CHAR;

  public boolean isValid(final String password) {
    return password.matches(PATTERN);
  }

}
