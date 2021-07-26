package com.pj.squashrestapp.util;

import static com.pj.squashrestapp.util.UsernameValidator.isValid;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Username validation")
class UsernameValidatorTest {

  @Test
  @DisplayName("Assert valid usernames")
  void assertValidUsernames() {
    assertAll(
        "Assert for TRUE",
        () -> assertTrue(isValid("ABCDEF")),
        () -> assertTrue(isValid("abcdef")),
        () -> assertTrue(isValid("aBcDeF")),
        () -> assertTrue(isValid("ABCDEF ghijkl")),
        () -> assertTrue(isValid("ABC_DEF")),
        () -> assertTrue(isValid("ABC-def")),
        () -> assertTrue(isValid("Abc Def")),
        () -> assertTrue(isValid("ęśąćż")),
        () -> assertTrue(isValid("Paweł")),
        () -> assertTrue(isValid("sørina_Ś")),
        () -> assertTrue(isValid("Seán P")),
        () -> assertTrue(isValid("Mathéo")),
        () -> assertTrue(isValid("Mát-yás")),
        () -> assertTrue(isValid("Renée")),
        () -> assertTrue(isValid("Adrián")),
        () -> assertTrue(isValid("Günther")));
  }

  @Test
  @DisplayName("Assert invalid usernames failure")
  void assertInvalidUsernames() {
    assertAll(
        "Assert for FALSE",
        () -> assertFalse(isValid("ABC*()")),
        () -> assertFalse(isValid("abc@def")),
        () -> assertFalse(isValid("abc @f")),
        () -> assertFalse(isValid("ab")),
        () -> assertFalse(isValid("A")),
        () -> assertFalse(isValid("A B")),
        () -> assertFalse(isValid("abcdefghi-abcdefghi-abcdefghi-")));
  }
}
