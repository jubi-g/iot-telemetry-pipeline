package com.itp.sensor_simulator.generator.strategy;

import com.itp.sensor_simulator.model.SensorTuning;
import com.itp.sensor_simulator.model.SensorType;

public interface ValueGenerator {
    SensorType supports();
    SensorTuning defaults();
    double generate();
}
