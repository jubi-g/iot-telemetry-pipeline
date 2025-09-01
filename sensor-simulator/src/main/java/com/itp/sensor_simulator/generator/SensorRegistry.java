package com.itp.sensor_simulator.generator;

import com.itp.sensor_simulator.model.Sensor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SensorRegistry {
    private final List<Sensor> sensors = new CopyOnWriteArrayList<>();

    public List<Sensor> all() {
        return List.copyOf(sensors);
    }

    public void setAll(List<Sensor> newSensors) {
        sensors.clear();
        sensors.addAll(newSensors);
    }
}
