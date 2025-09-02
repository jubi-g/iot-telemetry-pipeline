package com.itp.aggregate_service.repository;

import java.time.Instant;

public interface AggregateReadingRepository extends AggregateJdbcRepository {
    Instant oldestReading();
}
