package com.itp.aggregate_service.service;

import com.itp.aggregate_service.config.AggConfig;
import com.itp.aggregate_service.repository.impl.MinuteAggregateRepository;
import com.itp.aggregate_service.utils.TimeUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.itp.aggregate_service.utils.TimeUtil.laterOf;
import static com.itp.aggregate_service.utils.TimeUtil.latestUtcMinute;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinuteAggregationService implements AggregationService {
    private final AggConfig config;
    private final MinuteAggregateRepository repository;
    private final MeterRegistry metrics;

    @Override
    public Instant computeStartTime(Instant endTime) {
        if (!config.isCatchupOnStart()) return latestUtcMinute(endTime);
        Instant nextAfterLastAgg = Optional.ofNullable(repository.lastAggregatedSensor())
            .map(i -> latestUtcMinute(i.plus(1, ChronoUnit.MINUTES)))
            .orElse(null);
        Instant firstDataBucket = Optional.ofNullable(repository.oldestReading())
            .map(TimeUtil::latestUtcMinute)
            .orElse(null);

        // later of the two candidates; if both null -> endTime
        Instant candidate = laterOf(nextAfterLastAgg, firstDataBucket, endTime);
        return candidate.isAfter(endTime) ? latestUtcMinute(endTime) : latestUtcMinute(candidate);
    }

    @Override
    public void aggregateRangedWindow(Instant startTime, Instant endTime) {
        long t0 = System.nanoTime();
        int sensor = 0, group = 0, minutes = 0;

        // TODO: for_improvement; implement batch aggregations
        for (Instant bucket = startTime; !bucket.isAfter(endTime); bucket = bucket.plus(1, ChronoUnit.MINUTES)) {
            sensor += repository.upsertAggregate(bucket);
            group  += repository.upsertGroupAggregate(bucket);
            minutes++;
        }

        metrics.counter("agg.rows.sensor").increment(sensor);
        metrics.counter("agg.rows.group").increment(group);
        metrics.counter("agg.minutes.processed").increment(minutes);
        Timer.builder("agg.window.duration").register(metrics).record(System.nanoTime() - t0, TimeUnit.NANOSECONDS);

        log.info("Aggregated [{} .. {}] minutes={}, sensorRows={}, groupRows={}", startTime, endTime, minutes, sensor, group);
    }
}
