package com.itp.ingest.sensor_ingestion_service.model;

import java.time.Instant;

public record ReadingMessage(
        String sensorId,
        String sensorName,
        String type,
        String houseId,
        String zone,
        Instant timestamp,
        double value
) {}
