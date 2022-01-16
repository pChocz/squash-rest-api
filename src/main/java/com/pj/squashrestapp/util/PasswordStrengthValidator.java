package com.pj.squashrestapp.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class PasswordStrengthValidator {

  /** Note that this number must be smaller than MIN_LENGTH */
  private final int COMMON_STRING_THRESHOLD = 4;

  private final int MIN_LENGTH = 5;
  private final int MAX_LENGTH = 100;
  private final int MIN_NUMBER_OF_UNIQUE_CHARS_FOR_PASSWORD = 3;

  private final int MIN_LENGTH_FOR_LONG_PASSWORD = 20;

  private final List<String> COMMON_STRINGS =
      List.of(
          /* latin alphabet */
          "ABCDEFGHIKLMNOPQRSTVX",

          /* qwerty keyboard layout */
          "QWERTYUIOP",
          "ASDFGHJKL",
          "ZXCVBNM",

          /* numbers */
          "01234567890",

          /* other common phrases */
          "SQUASH",
          "PASSWORD");

  /**
   *
   *
   * <pre>
   *
   * Password is treated as valid if one of following cases is met:
   *
   * - contains  5 -  19 characters (out of which at least 3 unique) and is not 'common'
   *   (meaning it does not contain any 4-letter-long substring of any common phrases)
   *
   * - contains 20 - 100 characters (out of which at least 3 unique)
   *
   * </pre>
   */
  public boolean isValid(String password) {
    if (isNumberBetween(
        password.length(), MIN_LENGTH, MIN_LENGTH_FOR_LONG_PASSWORD - 1)) {

      return isValidStandardPassword(password);

    } else if (isNumberBetween(
        password.length(), MIN_LENGTH_FOR_LONG_PASSWORD, MAX_LENGTH)) {

      return isValidLongPassword(password);
    }

    return false;
  }

  private boolean isValidLongPassword(String password) {
    return numberOfUniqueCharacters(password) >= MIN_NUMBER_OF_UNIQUE_CHARS_FOR_PASSWORD;
  }

  private int numberOfUniqueCharacters(String string) {
    final Set<Character> uniqueCharacters = new HashSet<>();
    for (int i = 0; i < string.length(); i++) {
      uniqueCharacters.add(string.charAt(i));
    }
    return uniqueCharacters.size();
  }

  private static boolean isValidStandardPassword(String password) {
    return numberOfUniqueCharacters(password) >= MIN_NUMBER_OF_UNIQUE_CHARS_FOR_PASSWORD
        && !isCommonPassword(password);
  }

  private boolean isNumberBetween(final int num, final int min, final int max) {
    return num >= min
        && num <= max;
  }

  private static boolean isCommonPassword(String password) {
    final String passwordUppercase = password.toUpperCase();

    for (final String commonPhrase : COMMON_STRINGS) {
      if (containsCommonPhrase(passwordUppercase, commonPhrase)) {
        return true;
      }
    }

    // if it reached that point, it means that password is not common
    return false;
  }

  private static boolean containsCommonPhrase(String password, String commonPhrase) {
    for (int i = 0; i < password.length() - COMMON_STRING_THRESHOLD + 1; i++) {
      final String passwordSubstring = password.substring(i, i + COMMON_STRING_THRESHOLD);
      final String passwordSubstringReversed =
          new StringBuilder(passwordSubstring).reverse().toString();
      if (commonPhrase.contains(passwordSubstring)
          || commonPhrase.contains(passwordSubstringReversed)) {
        return true;
      }
    }
    // if it reached that point, it means that password
    // is not common comparing to current phrase
    return false;
  }
}
