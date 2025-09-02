package com.itp.aggregate_service.repository;

import java.time.Instant;

public interface MinuteAggregateRepository extends AggregateRepository, BatchAggregateRepository {
    Instant oldestReading();
}
