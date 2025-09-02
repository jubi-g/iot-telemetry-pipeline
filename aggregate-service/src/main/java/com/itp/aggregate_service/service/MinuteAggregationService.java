package com.itp.aggregate_service.service;

import com.itp.aggregate_service.config.AggConfig;
import com.itp.aggregate_service.repository.MinuteAggregateRepository;
import com.itp.aggregate_service.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.itp.aggregate_service.utils.TimeUtil.laterOf;
import static com.itp.aggregate_service.utils.TimeUtil.latestUtcMinute;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinuteAggregationService implements AggregationService {
    private final AggConfig config;
    private final MinuteAggregateRepository repository;

    @Override
    public Instant computeStartBucket(Instant latestBucket) {
        if (!config.isCatchupOnStart()) return latestBucket;
        Instant next = Optional.ofNullable(repository.lastAggregatedSensor())
            .map(i -> latestUtcMinute(i.plus(1, ChronoUnit.MINUTES))).orElse(null);
        Instant first = Optional.ofNullable(repository.oldestReading())
            .map(TimeUtil::latestUtcMinute).orElse(null);
        return laterOf(next, first, latestBucket);
    }

    @Override
    public void aggregateRange(Instant startBucket, Instant latestBucket) {
        int sensor = 0, group = 0;
        for (Instant bucket = startBucket; !bucket.isAfter(latestBucket); bucket = bucket.plus(1, ChronoUnit.MINUTES)) {
            sensor += repository.upsertAggregate(bucket);
            group  += repository.upsertGroupAggregate(bucket);
        }
        log.info("Aggregated up to {}, wrote sensorRows={}, groupRows={}", latestBucket, sensor, group);
    }
}
