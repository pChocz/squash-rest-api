package com.pj.squashrestapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

/** */
@UtilityClass
public class JacksonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static <T> Object deepCopy(final T object) {
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(object), object.getClass());
        } catch (JsonProcessingException e) {
            return "DEEP_COPY_ERROR";
        }
    }
}
