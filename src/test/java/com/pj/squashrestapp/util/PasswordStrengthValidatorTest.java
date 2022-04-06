package com.pj.squashrestapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.pj.squashrestapp.util.PasswordStrengthValidator.isValid;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Password strength validation")
class PasswordStrengthValidatorTest {

    @Test
    @DisplayName("Assert strong enough passwords")
    void assertValidPasswords() {
        assertAll(
                "Assert for TRUE",
                () -> assertTrue(isValid("AbcAbc!@#$%^&*()")),
                () -> assertTrue(isValid("abcABC")),
                () -> assertTrue(isValid("aBcABC")),
                () -> assertTrue(isValid("Abcabc")),
                () -> assertTrue(isValid("AbcAbc")),
                () -> assertTrue(isValid("aaabbbccc")),
                () -> assertTrue(isValid("Abc123!@#")),
                () -> assertTrue(isValid("abccf")),
                () -> assertTrue(isValid("aaabbbcccc")),
                () -> assertTrue(isValid("12334456")),
                () -> assertTrue(isValid("1232176****")),
                () -> assertTrue(isValid("1111111sdsadsa")),
                () -> assertTrue(isValid("1@#%%D fdsfs 4545434 f$#&*")),
                () -> assertTrue(isValid("squash-qwerty-1234567890")),
                () -> assertTrue(isValid("123456789123456789123")),
                () -> assertTrue(isValid("111111122222223333333")),
                () -> assertTrue(isValid("nvdjkwe1234")),
                () -> assertTrue(isValid("njfekjv0987")),
                () -> assertTrue(isValid("[{ #$%^ }]")));
    }

    @Test
    @DisplayName("Assert weak passwords failure")
    void assertInvalidPasswords() {
        assertAll(
                "Assert for FALSE",
                () -> assertFalse(isValid("ABcd")),
                () -> assertFalse(isValid("a")),
                () -> assertFalse(isValid(" ")),
                () -> assertFalse(isValid("ab D")),
                () -> assertFalse(isValid("A")),
                () -> assertFalse(isValid("A B")),
                () -> assertFalse(isValid("1234")),
                () -> assertFalse(isValid("ęął")),
                () -> assertFalse(isValid("abcdefgh")),
                () -> assertFalse(isValid("ABCDEF")),
                () -> assertFalse(isValid("POIUYT")),
                () -> assertFalse(isValid("jhgfd")),
                () -> assertFalse(isValid("hgfed")),
                () -> assertFalse(isValid("123456")),
                () -> assertFalse(isValid("squashapp")),
                () -> assertFalse(isValid("squash123")),
                () -> assertFalse(isValid("12345")),
                () -> assertFalse(isValid("111111111111111222222222222222")),
                () -> assertFalse(isValid("111111111111111111111111111111")),
                () -> assertFalse(isValid("1111111111")),
                () -> assertFalse(isValid("qwerty")),
                () -> assertFalse(isValid("qWerTy")),
                () -> assertFalse(isValid("qweRty")),
                () -> assertFalse(isValid("1234qwer")),
                () -> assertFalse(isValid("password1")),
                () -> assertFalse(isValid("sq5678")),
                () -> assertFalse(isValid("abCD12")));
    }
}
