package com.itp.sensor_simulator.generator.strategy;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThermostatGeneratorTest {
    private final ThermostatGenerator generator = new ThermostatGenerator();

    @Test
    void next_isAroundBaseline_andClampedAndRounded() {
        double value = generator.generate();

        var t = generator.defaults();
        assertThat(value).isBetween(t.min(), t.max());
        assertThat(Math.abs(value - t.baseline())).isLessThanOrEqualTo(t.drift() + 0.1);
        assertThat(Math.abs(value * 10 - Math.round(value * 10))).isLessThan(1e-9); // rounded to 1 decimal
    }

}