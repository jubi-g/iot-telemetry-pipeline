package com.itp.api_service.services.query.model;

import java.util.UUID;

public record SensorStatistics(
    UUID sensorId,
    Statistics stats
) {}
