package com.itp.sensor_simulator.generator.strategy;

import com.itp.sensor_simulator.model.SensorTuning;
import com.itp.sensor_simulator.model.SensorType;
import org.springframework.stereotype.Component;

import static com.itp.sensor_simulator.generator.util.Mathx.clamp;
import static com.itp.sensor_simulator.generator.util.Mathx.delta;
import static com.itp.sensor_simulator.generator.util.Mathx.round1;

@Component
public class ThermostatGenerator implements ValueGenerator {
    private static final SensorTuning DEF = new SensorTuning(22.5, 18.0, 30.0, 0.12);

    @Override
    public SensorType supports() {
        return SensorType.THERMOSTAT;
    }

    @Override
    public SensorTuning defaults() {
        return DEF;
    }

    @Override
    public double generate() {
        double value = DEF.baseline() + delta(DEF.drift());
        return round1(clamp(value, DEF.min(), DEF.max()));
    }
}
