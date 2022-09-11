package com.pj.squashrestapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/** */
@UtilityClass
public class GsonUtil {

    /**
     * Registers type adapters that deal with Date/Time:
     *
     * - LocalDate format specified by {@link GeneralUtil#DATE_FORMAT},
     * - LocalDateTime format specified by {@link GeneralUtil#DATE_TIME_FORMAT}
     * - ZonedDateTime format specified by {@link GeneralUtil#DATE_TIME_ISO_FORMAT}
     *
     * @return Gson object that can be used for serialization
     */
    public Gson gsonWithDateAndDateTime() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new GsonLocalDate())
                .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime())
                .registerTypeAdapter(ZonedDateTime.class, new GsonZonedDateTime())
                .setPrettyPrinting()
                .create();
    }
}
