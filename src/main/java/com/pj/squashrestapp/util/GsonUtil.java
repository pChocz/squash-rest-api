package com.pj.squashrestapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** */
@UtilityClass
public class GsonUtil {

    /**
     * Registers type adapters that deal with Date/Time:
     * - LocalDate format specified by {@link GeneralUtil#DATE_FORMAT},
     * - LocalDateTime format specified by {@link GeneralUtil#DATE_TIME_FORMAT}
     */
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new GsonLocalDate())
            .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime());

    public Gson gsonPrettyWithDateAndDateTime() {
        return GSON_BUILDER.setPrettyPrinting().create();
    }

    public String objectToJson(final Object object) {
        return GSON_BUILDER.create().toJson(object);
    }
}
