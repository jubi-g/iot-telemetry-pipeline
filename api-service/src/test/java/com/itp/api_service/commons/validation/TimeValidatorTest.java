package com.itp.api_service.commons.validation;

import com.itp.api_service._commons.validation.TimeValidator;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TimeValidatorTest {

    @Test
    void validWindow_passes() {
        Instant from = Instant.now().minusSeconds(3600);
        Instant to = Instant.now();
        assertDoesNotThrow(() -> TimeValidator.assertValid(from, to, Duration.ofHours(24)));
    }

    @Test
    void nullFromOrTo_throws() {
        Instant now = Instant.now();
        assertThrows(IllegalArgumentException.class,
                () -> TimeValidator.assertValid(null, now, Duration.ofHours(24)));
        assertThrows(IllegalArgumentException.class,
                () -> TimeValidator.assertValid(now, null, Duration.ofHours(24)));
    }

    @Test
    void fromNotBeforeTo_throws() {
        Instant now = Instant.now();
        assertThrows(IllegalArgumentException.class,
                () -> TimeValidator.assertValid(now, now, Duration.ofHours(24)));
        assertThrows(IllegalArgumentException.class,
                () -> TimeValidator.assertValid(now, now.minusSeconds(10), Duration.ofHours(24)));
    }

    @Test
    void windowExceedsMax_throws() {
        Instant from = Instant.now().minus(Duration.ofDays(2));
        Instant to = Instant.now();
        assertThrows(IllegalArgumentException.class,
                () -> TimeValidator.assertValid(from, to, Duration.ofHours(24)));
    }

}
