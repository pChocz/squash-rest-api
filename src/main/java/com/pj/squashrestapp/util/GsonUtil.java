package com.pj.squashrestapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 */
@UtilityClass
public class GsonUtil {

  /**
   * Registers type adapter that deals with LocalDate format
   * specified by {@link GeneralUtil#DATE_FORMAT},
   *
   * as well as type adapter that deals with LocalDateTime format
   * specified by {@link GeneralUtil#DATE_TIME_FORMAT}
   *
   * @return Gson object that can be used for serialization
   */
  public Gson gsonWithDateAndDateTime() {
    return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, getLocalDateJsonDeserializer())
            .registerTypeAdapter(LocalDateTime.class, getLocalDateTimeJsonDeserializer())
            .setPrettyPrinting()
            .create();
  }

  private JsonDeserializer<LocalDate> getLocalDateJsonDeserializer() {
    return (JsonDeserializer<LocalDate>) (jsonElement, type, context)
            -> LocalDate.parse(
            jsonElement.getAsString(),
            DateTimeFormatter.ofPattern(GeneralUtil.DATE_FORMAT));
  }

  private JsonDeserializer<LocalDateTime> getLocalDateTimeJsonDeserializer() {
    return (JsonDeserializer<LocalDateTime>) (jsonElement, type, context)
            -> LocalDateTime.parse(
            jsonElement.getAsString(),
            DateTimeFormatter.ofPattern(GeneralUtil.DATE_TIME_FORMAT));
  }

}
