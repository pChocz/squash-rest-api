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
     * Registers type adapter that deals with LocalDate format specified by {@link
     * GeneralUtil#DATE_FORMAT},
     *
     * <p>as well as type adapter that deals with LocalDateTime format specified by {@link
     * GeneralUtil#DATE_TIME_FORMAT}
     *
     * @return Gson object that can be used for serialization
     */
    public Gson gsonWithDateAndDateTime() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new GsonLocalDate())
                .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTime())
                .setPrettyPrinting()
                .create();
    }
}
