package com.itp.sensor_simulator.generator.strategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FuelGeneratorTest {

    private final FuelGenerator generator = new FuelGenerator();

    @Test
    void next_withinBounds_andRoundedTo1dp() {
        double value = generator.generate();
        var t = generator.defaults();

        assertThat(value).isBetween(t.min(), t.max());

        double oneDecimal = Math.round(value * 10.0) / 10.0;
        assertThat(value).isEqualTo(oneDecimal);
    }

    @Test
    void next_variesAroundBaseline() {
        double value = generator.generate();
        var t = generator.defaults();

        // not required to be exactly at baseline, but should not exceed baseline ± (drift + rounding step)
        double maxExpectedDeviation = t.drift() + 0.1; // allow 0.1 for 1dp rounding
        assertThat(Math.abs(value - t.baseline())).isLessThanOrEqualTo(maxExpectedDeviation);
    }

}