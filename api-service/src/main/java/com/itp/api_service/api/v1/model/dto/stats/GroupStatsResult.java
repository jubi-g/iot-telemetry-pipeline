package com.itp.api_service.api.v1.model.dto.stats;

import com.itp.api_service.services.query.model.GroupStatistics;

public record GroupStatsResult(
    String houseNumber,
    String zoneNumber,
    String sensorType,
    StatsResult statistics
) {
    public static GroupStatsResult of(GroupStatistics result) {
        return new GroupStatsResult(result.houseId(), result.zone(), result.type(), StatsResult.of(result.stats()));
    }
}

