package com.itp.sensor_simulator.generator.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MathxTest {

    @Nested
    class clamp {
        @Test
        void clamp_returnsValueWithinBounds() {
            assertThat(Mathx.clamp(5.0, 0.0, 10.0)).isEqualTo(5.0);
        }

        @Test
        void clamp_returnsLowerBound_whenBelow() {
            assertThat(Mathx.clamp(-3.0, 0.0, 10.0)).isEqualTo(0.0);
        }

        @Test
        void clamp_returnsUpperBound_whenAbove() {
            assertThat(Mathx.clamp(15.0, 0.0, 10.0)).isEqualTo(10.0);
        }

        @Test
        void clamp_handlesEqualBounds() {
            assertThat(Mathx.clamp(42.0, 7.0, 7.0)).isEqualTo(7.0);
        }
    }


    @Nested
    class round1 {
        @Test
        void round1_roundsHalfUp() {
            assertThat(Mathx.round1(22.456)).isEqualTo(22.5);
        }

        @Test
        void round1_roundsDown() {
            assertThat(Mathx.round1(19.04)).isEqualTo(19.0);
        }

        @Test
        void round1_handlesNegativeValues() {
            assertThat(Mathx.round1(-3.26)).isEqualTo(-3.3);
            assertThat(Mathx.round1(-3.24)).isEqualTo(-3.2);
        }
    }

    // despite repeated, always in range
    @RepeatedTest(5)
    void delta_producesValuesWithinRange() {
        double drift = 0.5;
        double d = Mathx.delta(drift);
        assertThat(d).isBetween(-drift, drift);
    }
}