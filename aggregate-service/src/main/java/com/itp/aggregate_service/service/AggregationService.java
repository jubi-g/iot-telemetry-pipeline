package com.itp.aggregate_service.service;

import java.time.Instant;

public interface AggregationService {
    Instant computeStartBucket(Instant latestBucket);
    void aggregateRange(Instant startBucket, Instant latestBucket);
}
