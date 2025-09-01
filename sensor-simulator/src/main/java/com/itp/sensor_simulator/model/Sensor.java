package com.itp.sensor_simulator.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record Sensor(
        String id,
        String name,
        SensorType type,
        String zone,
        String houseId
) {
    public Sensor {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

    public static Sensor of(String name, SensorType type, String zone, String houseId) {
        return new Sensor(null, name, type, zone, houseId);
    }
}
