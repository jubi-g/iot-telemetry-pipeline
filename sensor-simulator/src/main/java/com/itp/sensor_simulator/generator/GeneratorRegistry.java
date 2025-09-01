package com.itp.sensor_simulator.generator;

import com.itp.sensor_simulator.generator.strategy.ValueGenerator;
import com.itp.sensor_simulator.model.SensorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GeneratorRegistry {
    private final Map<SensorType, ValueGenerator> registry;

    public GeneratorRegistry(List<ValueGenerator> generators) {
        this.registry = generators.stream().collect(Collectors.toMap(ValueGenerator::supports, s -> s));
    }

    public ValueGenerator forType(SensorType type) {
        var generator = registry.get(type);
        if (generator == null) {
            throw new IllegalArgumentException("No generator registered for sensor type: " + type);
        }
        return generator;
    }
}
