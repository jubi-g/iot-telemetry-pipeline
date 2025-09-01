package com.itp.sensor_simulator.model.dto;

import com.itp.sensor_simulator.model.Sensor;
import com.itp.sensor_simulator.model.SensorType;

import java.time.Instant;

public record Reading(
    String sensorId,
    String sensorName,
    String houseId,
    String zone,
    SensorType type,
    Instant timestamp,
    double value
) {
    public static Reading of(Sensor sensor, Instant timestamp, double nextValue) {
        return new Reading(sensor.id(), sensor.name(), sensor.houseId(), sensor.zone(), sensor.type(), timestamp, nextValue);
    }
}
