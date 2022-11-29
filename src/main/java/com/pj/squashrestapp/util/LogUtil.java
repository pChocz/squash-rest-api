package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.argument.StructuredArguments.v;

@Slf4j
@UtilityClass
@SuppressWarnings("PlaceholderCountMatchesArgumentCount")
public class LogUtil {

    public static void logCreate(final Object object) {
        log.info(
                "CREATED",
                v("className", object.getClass().getSimpleName()),
                v("object", object)
        );
    }

    public static void logDelete(final Object object) {
        log.info(
                "DELETED",
                v("className", object.getClass().getSimpleName()),
                v("object", object)
        );
    }

    public static void logModify(final Object objectBefore, final Object objectAfter) {
        log.info(
                "MODIFIED",
                v("className", objectAfter.getClass().getSimpleName()),
                v("objectBefore", objectBefore),
                v("objectAfter", objectAfter)
        );
    }
}
