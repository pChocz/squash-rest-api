package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class RoundingUtil {

    public BigDecimal round(final float number) {
        return round(number, 2);
    }

    public BigDecimal round(final float number, final int decimalPlaces) {
        return new BigDecimal(number).setScale(decimalPlaces, RoundingMode.HALF_UP);
    }
}
