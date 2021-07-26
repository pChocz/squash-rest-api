package com.pj.squashrestapp.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("General util tests")
class GeneralUtilTest {

  @Test
  @DisplayName("Assert build proper username")
  void assertValidUsernames() {
    assertAll(
        "Assert for TRUE",
        () -> assertEquals("Stefan D", GeneralUtil.buildProperUsername("stefan d")),
        () -> assertEquals("Qwer Rty", GeneralUtil.buildProperUsername("QWER RTY")),
        () -> assertEquals("A S D Fis", GeneralUtil.buildProperUsername("a s D fis")),
        () ->
            assertEquals("Stefan D Fds S", GeneralUtil.buildProperUsername("  Stefan     D FDS s")),
        () -> assertEquals("Pjo_Ter", GeneralUtil.buildProperUsername("  PJO_TER ")),
        () -> assertEquals("Asd Def", GeneralUtil.buildProperUsername("Asd  Def")),
        () -> assertEquals("Pjo-Te-Ro", GeneralUtil.buildProperUsername("pjo-TE-Ro")),
        () -> assertEquals("Pjo __ -- Ter", GeneralUtil.buildProperUsername("pjO __ -- TEr")),
        () -> assertEquals("Qwe@_Rty", GeneralUtil.buildProperUsername("QWE@_rty")));
  }
}
