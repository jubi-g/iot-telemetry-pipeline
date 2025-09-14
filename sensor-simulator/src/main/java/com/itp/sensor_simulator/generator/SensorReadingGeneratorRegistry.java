package com.itp.sensor_simulator.generator;

import com.itp.sensor_simulator.generator.sensors.SensorReadingGenerator;
import com.itp.sensor_simulator.model.SensorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SensorReadingGeneratorRegistry {
    private final Map<SensorType, SensorReadingGenerator> registry;

    public SensorReadingGeneratorRegistry(List<SensorReadingGenerator> generators) {
        this.registry = generators.stream()
            .collect(Collectors
                .toMap(SensorReadingGenerator::supports, s -> s));
    }

    public SensorReadingGenerator forType(SensorType type) {
        var generator = registry.get(type);
        if (generator == null) {
            throw new IllegalArgumentException("No generator registered for sensor type: " + type);
        }
        return generator;
    }
}
