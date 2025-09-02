package com.itp.aggregate_service.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeUtil {
    private TimeUtil() {}

    public static Instant latestUtcMinute(Instant t) {
        return t.truncatedTo(ChronoUnit.MINUTES);
    }

    public static Instant laterOf(Instant a, Instant b, Instant defaultValue) {
        if (a == null && b == null) return defaultValue;
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }
}
