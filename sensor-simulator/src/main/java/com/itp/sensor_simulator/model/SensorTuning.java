package com.itp.sensor_simulator.model;

import lombok.Builder;

@Builder
public record SensorTuning(
        double baseline,    // "center" value
        double min,         // hard floor
        double max,         // hard ceiling
        double drift        // max per-tick change
) {}
