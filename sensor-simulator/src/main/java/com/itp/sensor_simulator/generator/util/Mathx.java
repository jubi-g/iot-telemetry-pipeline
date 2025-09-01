package com.itp.sensor_simulator.generator.util;

import java.util.concurrent.ThreadLocalRandom;

public final class Mathx {
    private Mathx() {}

    /**
     * Clamp a value between minimum and maximum
     *      if value < lo, return lo
     *      if value > hi, return hi
     *      otherwise, return value
     */
    public static double clamp(double value, double lo, double hi) {
        return Math.max(lo, Math.min(hi, value));
    }

    /**
     * Round a value to 1 decimal place
     *      e.g. 22.456 -> 22.5, 19.04 -> 19.0
     */
    public static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /**
     * Generates a random delta value within the range {@code [-drift, +drift]}
     *  Used to simulate small sensor noise or variation around a baseline value
     */
    public static double delta(double drift) {
        var rnd = ThreadLocalRandom.current();
        return rnd.nextDouble(-drift, drift);
    }

}
