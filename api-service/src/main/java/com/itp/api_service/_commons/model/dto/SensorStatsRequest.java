package com.itp.api_service._commons.model.dto;

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
}
