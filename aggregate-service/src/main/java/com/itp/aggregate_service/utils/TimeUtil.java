package com.itp.aggregate_service.utils;

import java.time.Instant;

public class TimeUtil {
    private TimeUtil() {}

    public static Instant truncateToMinute(Instant instant) {
        long epoch = instant.getEpochSecond();
        long floored = (epoch / 60) * 60;
        return Instant.ofEpochSecond(floored);
    }

    public static Instant latestUtcMinute(Instant t) {
        long s = t.getEpochSecond();
        return Instant.ofEpochSecond((s / 60) * 60);
    }

    public static Instant laterOf(Instant a, Instant b, Instant defaultValue) {
        if (a == null && b == null) return defaultValue;
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }
}
