package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;

/**
 *
 */
@UtilityClass
public class PasswordStrengthValidator {

  private final int MIN_LENGTH = 5;
  private final int MAX_LENGTH = 100;
  private final String MIN_MAX_CHAR = ".{" + MIN_LENGTH + "," + MAX_LENGTH + "}";

  private final String PATTERN = MIN_MAX_CHAR;

  public boolean isValid(final String password) {
    return password.matches(PATTERN);
  }

}
