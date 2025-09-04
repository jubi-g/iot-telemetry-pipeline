package com.itp.api_service.api.v1.model.dto.stats;

import com.itp.api_service.services.query.model.SensorStatistics;

import java.util.UUID;

public record SensorStatsResult(
    UUID sensorId,
    String sensorType,
    StatsResult statistics
) {
    public static SensorStatsResult of(SensorStatistics result) {
        return new SensorStatsResult(result.sensorId(), result.stats().type(), StatsResult.of(result.stats()));
    }
}
