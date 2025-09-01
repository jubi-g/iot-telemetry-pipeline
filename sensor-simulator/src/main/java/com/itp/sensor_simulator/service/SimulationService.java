package com.itp.sensor_simulator.service;

import com.itp.sensor_simulator.client.ReadingProducer;
import com.itp.sensor_simulator.generator.GeneratorRegistry;
import com.itp.sensor_simulator.model.Sensor;
import com.itp.sensor_simulator.model.dto.Reading;
import com.itp.sensor_simulator.generator.SensorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SimulationService {
    private final SensorRegistry registry;
    private final GeneratorRegistry generators;
    private final ReadingProducer producer;

    @Scheduled(fixedRate = 1000, initialDelay = 2000)
    public void simulate() {
        Instant now = Instant.now();
        for (Sensor sensor : registry.all()) {
            double value = generators.forType(sensor.type()).generate();
            var reading = Reading.of(sensor, now, value);
            producer.send(reading);
        }
    }
}
