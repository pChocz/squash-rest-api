package com.pj.squashrestapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.pj.squashrestapp.util.PasswordStrengthValidator.isValid;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            () -> assertTrue(isValid("abcdefgh")),
            () -> assertTrue(isValid("ABCDEF")),
            () -> assertTrue(isValid("123456")),
            () -> assertTrue(isValid("qwerty")),
            () -> assertTrue(isValid("1@#%%D fdsfs 4545434 f$#&*")),
            () -> assertTrue(isValid("[{ #$%^ }]"))
    );
  }

  @Test
  @DisplayName("Assert weak passwords failure")
  void assertInvalidUsernames() {
    assertAll("Assert for FALSE",
            () -> assertFalse(isValid("ABcd")),
            () -> assertFalse(isValid("a")),
            () -> assertFalse(isValid(" ")),
            () -> assertFalse(isValid("ab D")),
            () -> assertFalse(isValid("A")),
            () -> assertFalse(isValid("A B")),
            () -> assertFalse(isValid("1234")),
            () -> assertFalse(isValid("ęął"))
    );
  }

}