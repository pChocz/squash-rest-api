package com.pj.squashrestapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.pj.squashrestapp.util.PasswordStrengthValidator.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password strength validation")
class PasswordStrengthValidatorTest {

  @Test
  @DisplayName("Assert strong enough passwords")
  void assertValidUsernames() {
    assertAll("Assert for TRUE",
            () -> assertTrue(isValid("abcABC")),
            () -> assertTrue(isValid("aBcABC")),
            () -> assertTrue(isValid("Abcabc")),
            () -> assertTrue(isValid("AbcAbc")),
            () -> assertTrue(isValid("AbcAbc!@#$%^&*()")),
            () -> assertTrue(isValid("Abc123!@#")),
            () -> assertTrue(isValid("Abc123"))
    );
  }

  @Test
  @DisplayName("Assert weak passwords failure")
  void assertInvalidUsernames() {
    assertAll("Assert for FALSE",
            () -> assertFalse(isValid("Abc")),
            () -> assertFalse(isValid("ABC def")),
            () -> assertFalse(isValid("abcdef")),
            () -> assertFalse(isValid("ABCDEF")),
            () -> assertFalse(isValid("123456")),
            () -> assertFalse(isValid("A B"))
    );
  }

}