package com.example.kafka;

import java.time.Instant;
import java.util.Date;

import org.springframework.lang.NonNull;

public class DateUtils {
    public static Date toDate(@NonNull Instant instant) {
        return Date.from(instant);
    }
    public static long toEpochSeconds(@NonNull Instant instant) {
        return instant.toEpochMilli();
    }
}
