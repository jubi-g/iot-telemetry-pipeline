package com.itp.api_service._commons.validation;

import java.time.Duration;
import java.time.Instant;

public final class TimeValidator {
    private TimeValidator() {}

    public static void assertValid(Instant from, Instant to, Duration maxWindow) {
        if (from == null || to == null) throw new IllegalArgumentException("from/to required");
        if (!from.isBefore(to)) throw new IllegalArgumentException("from must be before to");
        if (Duration.between(from, to).compareTo(maxWindow) > 0)
            throw new IllegalArgumentException("time window exceeds " + maxWindow);
    }
}
