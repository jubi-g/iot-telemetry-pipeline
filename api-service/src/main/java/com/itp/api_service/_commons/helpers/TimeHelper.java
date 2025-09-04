package com.itp.api_service._commons.helpers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class TimeHelper {
    private TimeHelper() {}

    public static OffsetDateTime odt(Instant ts) {
        return OffsetDateTime.ofInstant(ts, ZoneOffset.UTC);
    }
}
