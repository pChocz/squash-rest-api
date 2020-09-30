package com.pj.squashrestapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 */
@UtilityClass
public class GsonUtil {

  /**
   * registers type adapter that deals with LocalDate format
   * specified by {@link GeneralUtil#DATE_FORMAT}
   *
   * @return Gson object that can be used for serialization
   */
  public Gson gsonWithDate() {
    return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, getLocalDateJsonDeserializer())
            .create();
  }

  private JsonDeserializer<LocalDate> getLocalDateJsonDeserializer() {
    return (JsonDeserializer<LocalDate>) (jsonElement, type, context)
            -> LocalDate.parse(
            jsonElement.getAsString(),
            DateTimeFormatter.ofPattern(GeneralUtil.DATE_FORMAT));
  }

}
