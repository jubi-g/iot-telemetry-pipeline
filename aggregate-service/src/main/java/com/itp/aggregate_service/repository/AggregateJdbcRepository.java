package com.itp.aggregate_service.repository;

import java.time.Instant;

public interface AggregateJdbcRepository {
    Instant lastAggregatedSensor();
    int upsertAggregate(Instant bucket);
    int upsertGroupAggregate(Instant bucket);
}
