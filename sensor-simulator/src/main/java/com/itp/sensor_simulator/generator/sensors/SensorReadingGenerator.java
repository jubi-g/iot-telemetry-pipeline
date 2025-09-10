package com.itp.sensor_simulator.generator.sensors;

import com.itp.sensor_simulator.model.SensorTuning;
import com.itp.sensor_simulator.model.SensorType;

public interface SensorReadingGenerator {
    SensorType supports();
    SensorTuning defaults();
    double generate();
}
