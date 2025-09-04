package com.itp.api_service._commons.model.dto;

import com.itp.api_service.api.v1.model.dto.stats.StatsQueryParams;
import com.itp.api_service.services.query.model.dto.TimeWindowRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SensorStatsRequest implements CacheKeyAware {
    private UUID sensorId;
    private TimeWindowRequest window;

    @Override
    public String cacheKey() {
        return "sensor|" + sensorId + "|" + window.cacheKey();
    }

    public static SensorStatsRequest of(UUID sensorId, StatsQueryParams request) {
        return new SensorStatsRequest(sensorId, new TimeWindowRequest(request.from(), request.to()));
    }
}
