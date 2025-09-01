package com.itp.sensor_simulator.client;

import com.itp.sensor_simulator.model.dto.Reading;

public interface ReadingProducer {
    void send(Reading reading);
}
