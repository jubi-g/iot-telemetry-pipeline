package com.itp.aggregate_service.service;

import java.time.Instant;

public interface AggregationService {
    Instant computeStartTime(Instant endTime);
    void aggregateRangedWindow(Instant startTime, Instant endTime);
}
