package com.itp.sensor_simulator.seeding.seeders;

import com.itp.sensor_simulator.config.AppConfig;
import com.itp.sensor_simulator.generator.SensorRegistry;
import com.itp.sensor_simulator.model.SeedType;
import com.itp.sensor_simulator.model.Sensor;
import com.itp.sensor_simulator.model.SensorType;
import com.itp.sensor_simulator.seeding.Seeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class DefaultSeeder implements Seeder {
    private final AppConfig appConfig;
    private final SensorRegistry sensorRegistry;

    @Override
    public SeedType supports() {
        return SeedType.DEFAULT;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public void seed() {
        List<String> zonesPool = appConfig.getZones();
        List<Sensor> seeded = new ArrayList<>();

        int sensorCount = appConfig.getPerSensorCount() != null ? appConfig.getPerSensorCount() : 3;
        int houses = appConfig.getHousesCount() != null ? appConfig.getHousesCount() : 3;
        for (int h = 1; h <= houses; h++) {
            String houseId = generateName("houseId-", h, 0);
            for (int s = 1; s <= sensorCount; s++) {
                seeded.add(Sensor.of(generateName("thermostat-", h, s), SensorType.THERMOSTAT, pickZone(zonesPool, 0, "ZoneA"), houseId));
                seeded.add(Sensor.of(generateName("hr-", h, s), SensorType.HEART_RATE, pickZone(zonesPool, 1, "ZoneB"), houseId));
                seeded.add(Sensor.of(generateName("fuel-", h, s), SensorType.FUEL_CONSUMPTION, pickZone(zonesPool, 2, "ZoneC"), houseId));
            }
        }

        sensorRegistry.setAll(seeded);

        log.info("Seeded {} houses, {} sensors across zones {}", houses, seeded.size(), zonesPool);
    }

    private String pickZone(List<String> zones, int idx, String fallback) {
        return (zones != null && !zones.isEmpty()) ? zones.get(idx % zones.size()) : fallback;
    }

    private String generateName(String prefix, Integer count, Integer sensorCount) {
        return prefix + count + sensorCount;
    }

}
