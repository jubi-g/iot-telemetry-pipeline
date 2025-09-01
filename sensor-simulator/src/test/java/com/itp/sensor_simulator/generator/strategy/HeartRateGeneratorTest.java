package com.itp.sensor_simulator.generator.strategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeartRateGeneratorTest {

    private final HeartRateGenerator generator = new HeartRateGenerator();

    @Test
    void next_withinBounds_andInteger() {
        double value = generator.generate();
        var t = generator.defaults();

        assertThat(value).isBetween(t.min(), t.max());
        assertThat(value % 1.0).isZero();
    }

}