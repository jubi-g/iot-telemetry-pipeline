package com.itp.aggregate_service.repository;

import java.time.Instant;
import java.util.List;

public interface BatchAggregateRepository {
    int[] upsertBatchAggregate(List<Instant> buckets);
    int[] upsertBatchGroupAggregate(List<Instant> buckets);
}
