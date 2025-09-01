package com.itp.sensor_simulator.generator.strategy;

import com.itp.sensor_simulator.model.SensorTuning;
import com.itp.sensor_simulator.model.SensorType;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

import static com.itp.sensor_simulator.generator.util.Mathx.clamp;
import static com.itp.sensor_simulator.generator.util.Mathx.delta;

@Component
public class HeartRateGenerator implements ValueGenerator {
    private static final SensorTuning DEF = new SensorTuning(76.0, 55.0, 165.0, 2.2);

    @Override
    public SensorType supports() {
        return SensorType.HEART_RATE;
    }

    @Override
    public SensorTuning defaults() {
        return DEF;
    }

    @Override
    public double generate() {
        var rnd = ThreadLocalRandom.current();
        double delta_v = delta(DEF.drift());

        // occasional movement/workout spike
        if (rnd.nextDouble() < 0.05) {
            delta_v += rnd.nextDouble(5.0, 12.0);
        }

        double v = DEF.baseline() + delta_v;
        return Math.round(clamp(v, DEF.min(), DEF.max()));
    }
}
