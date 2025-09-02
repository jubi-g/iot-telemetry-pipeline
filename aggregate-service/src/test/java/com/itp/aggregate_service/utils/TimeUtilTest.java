package com.itp.aggregate_service.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilTest {

    @Nested
    class latestUtcMinute {
        @Test
        void latestUtcMinute_floorsPositiveInstant() {
            Instant t = Instant.parse("2025-09-02T11:22:33Z");
            assertThat(TimeUtil.latestUtcMinute(t))
                .isEqualTo(Instant.parse("2025-09-02T11:22:00Z"));
        }

        @Test
        void latestUtcMinute_isIdempotentOnBoundary() {
            Instant boundary = Instant.parse("2025-09-02T11:22:00Z");
            assertThat(TimeUtil.latestUtcMinute(boundary))
                .isEqualTo(boundary);
        }

        @Test
        void latestUtcMinute_handlesPreEpochCorrectly() {
            // 1969-12-31T23:59:59Z -> 1969-12-31T23:59:00Z
            Instant preEpoch = Instant.parse("1969-12-31T23:59:59Z");
            assertThat(TimeUtil.latestUtcMinute(preEpoch))
                .isEqualTo(Instant.parse("1969-12-31T23:59:00Z"));
        }

        @Test
        void latestUtcMinute_zerosNanos() {
            Instant withNanos = Instant.parse("2025-09-02T11:22:33.123456Z");
            Instant floored = TimeUtil.latestUtcMinute(withNanos);
            assertThat(floored.getEpochSecond()).isEqualTo(Instant.parse("2025-09-02T11:22:00Z").getEpochSecond());
            assertThat(floored.getNano()).isZero();
        }
    }

    @Nested
    class laterOf {
        @Test
        void laterOf_returnsDefaultWhenBothNull() {
            Instant def = Instant.parse("2025-09-02T11:21:00Z");
            assertThat(TimeUtil.laterOf(null, null, def)).isEqualTo(def);
        }

        @Test
        void laterOf_returnsNonNullWhenOtherIsNull() {
            Instant a = Instant.parse("2025-09-02T11:20:00Z");
            Instant b = Instant.parse("2025-09-02T11:25:00Z");
            assertThat(TimeUtil.laterOf(a, null, Instant.EPOCH)).isEqualTo(a);
            assertThat(TimeUtil.laterOf(null, b, Instant.EPOCH)).isEqualTo(b);
        }

        @Test
        void laterOf_picksLaterInstant() {
            Instant earlier = Instant.parse("2025-09-02T11:20:00Z");
            Instant later = Instant.parse("2025-09-02T11:21:00Z");
            assertThat(TimeUtil.laterOf(earlier, later, Instant.EPOCH)).isEqualTo(later);
        }

        @Test
        void laterOf_whenEqual_returnsEitherButEqualToThatInstant() {
            Instant t = Instant.parse("2025-09-02T11:21:00Z");
            assertThat(TimeUtil.laterOf(t, t, Instant.EPOCH)).isEqualTo(t);
        }

        @Test
        void laterOf_handlesNonMinuteInputs() {
            // laterOf should compare exact instants, not truncated; caller decides truncation policy.
            Instant a = Instant.parse("2025-09-02T11:21:30Z");
            Instant b = Instant.parse("2025-09-02T11:21:29Z");
            assertThat(TimeUtil.laterOf(a, b, Instant.EPOCH)).isEqualTo(a);
        }
    }

}