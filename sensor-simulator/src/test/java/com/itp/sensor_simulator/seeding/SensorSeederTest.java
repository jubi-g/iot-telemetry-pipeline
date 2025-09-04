package com.itp.sensor_simulator.seeding;

import com.itp.sensor_simulator.config.AppConfig;
import com.itp.sensor_simulator.generator.SensorRegistry;
import com.itp.sensor_simulator.model.Sensor;
import com.itp.sensor_simulator.model.SensorType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensorSeederTest {

    @Test
    void seedsExpectedCounts() {
        var props = new AppConfig();
        props.setHousesCount(2);
        props.setPerSensorCount(3);
        props.setZones(java.util.List.of("ZoneA","ZoneB","ZoneC"));

        var registry = new SensorRegistry();
        var seeder = new SensorSeeder(props, registry);
        seeder.seed();

        var sensors = registry.all();
        assertThat(sensors).isNotEmpty();
        assertThat(sensors.size()).isEqualTo(2 * (3 * 3)); // 2 houses × 3 fixed sensors * 3 per-sensor
        assertThat(sensors.stream()
                .map(Sensor::type).distinct())
                .containsExactlyInAnyOrder(SensorType.THERMOSTAT, SensorType.HEART_RATE, SensorType.FUEL_CONSUMPTION);
    }

}