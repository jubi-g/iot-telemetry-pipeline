package com.itp.api_service.api.v1.model.dto.stats;

import com.itp.api_service.services.query.model.Statistics;

public record StatsResult(
    long totalCount,
    Double min,
    Double max,
    Double avg,
    Double median
) {
    public static StatsResult of(Statistics result) {
        return new StatsResult(result.totalCount(), result.min(), result.max(), result.avg(), result.median());
    }
}
