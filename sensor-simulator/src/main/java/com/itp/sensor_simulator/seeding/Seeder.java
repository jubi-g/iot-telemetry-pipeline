package com.itp.sensor_simulator.seeding;

import com.itp.sensor_simulator.model.SeedType;

public interface Seeder {
    SeedType supports();
    boolean isDefault();
    void seed();
}
