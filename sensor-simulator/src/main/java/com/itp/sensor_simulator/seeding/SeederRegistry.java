package com.itp.sensor_simulator.seeding;

import com.itp.sensor_simulator.config.AppConfig;
import com.itp.sensor_simulator.model.SeedType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeederRegistry {

    private final List<Seeder> seeders;
    private final AppConfig props;

    public SeederRegistry(List<Seeder> seeders, AppConfig props) {
        this.seeders = seeders;
        this.props = props;
    }

    private Seeder pickSeeder() {
        SeedType desired = props.getSeedType();
        return seeders.stream()
            .filter(s -> s.supports() == desired)
            .findFirst()
            .orElseGet(() -> seeders.stream()
                .filter(Seeder::isDefault)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No default seeder")));
    }

    @PostConstruct
    public void seedOnStartup() {
        pickSeeder().seed();
    }

}
