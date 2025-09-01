package com.itp.sensor_simulator.model.dto;

import com.itp.sensor_simulator.model.SensorType;
import com.itp.sensor_simulator.model.Sensor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class Reading {
    private String sensorId;
    private String sensorName;
    private String houseId;
    private String zone;
    private SensorType type;
    private Instant timestamp;
    private double value;

    public static Reading of(Sensor sensor, Instant timestamp, double nextValue) {
        return new Reading(sensor.id(), sensor.name(), sensor.houseId(), sensor.zone(), sensor.type(), timestamp, nextValue);
    }
}
