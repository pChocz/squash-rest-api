package com.pj.squashrestapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.pj.squashrestapp.util.UsernameValidator.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Username validation")
class UsernameValidatorTest {

  @Test
  @DisplayName("Assert valid usernames")
  void assertValidUsernames() {
    assertAll("Assert for TRUE",
            () -> assertTrue(isValid("ABCDEF")),
            () -> assertTrue(isValid("abcdef")),
            () -> assertTrue(isValid("aBcDeF")),
            () -> assertTrue(isValid("ABCDEF ghijkl")),
            () -> assertTrue(isValid("ABC_DEF")),
            () -> assertTrue(isValid("ABC-def")),
            () -> assertTrue(isValid("Abc Def"))
    );
  }

  @Test
  @DisplayName("Assert invalid usernames failure")
  void assertInvalidUsernames() {
    assertAll("Assert for FALSE",
            () -> assertFalse(isValid("ABC*()")),
            () -> assertFalse(isValid("abc@def")),
            () -> assertFalse(isValid("abc @f")),
            () -> assertFalse(isValid("ab")),
            () -> assertFalse(isValid("A")),
            () -> assertFalse(isValid("A B")),
            () -> assertFalse(isValid("abcdefghi-abcdefghi-abcdefghi-"))
    );
  }

}