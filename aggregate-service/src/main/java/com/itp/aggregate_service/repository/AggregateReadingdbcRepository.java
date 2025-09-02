package com.itp.aggregate_service.repository;

import java.time.Instant;

public interface AggregateReadingdbcRepository extends AggregateJdbcRepository {
    Instant oldestReading();
}
