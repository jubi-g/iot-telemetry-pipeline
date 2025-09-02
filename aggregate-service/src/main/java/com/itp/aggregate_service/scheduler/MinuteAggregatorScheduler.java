package com.itp.aggregate_service.scheduler;

import com.itp.aggregate_service.config.AggConfig;
import com.itp.aggregate_service.service.AggregationService;
import com.itp.aggregate_service.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinuteAggregatorScheduler implements Scheduler{

    private final AggConfig aggConfig;
    private final AggregationService service;

    @Override
    @Scheduled(fixedDelayString = "30000", initialDelayString = "15000")
    public void runSchedule() {
        log.info("Aggregator tick at {}", Instant.now());
        try {
            Instant now = Instant.now().minusSeconds(aggConfig.getDelaySeconds());
            Instant latestBucket = TimeUtil.truncateToMinute(now);
            Instant startBucket = service.computeStartBucket(latestBucket);
            if (startBucket.isAfter(latestBucket)) {
                log.info("Nothing to aggregate: startBucket={} > latestBucket={}", startBucket, latestBucket);
                return;
            }
            service.aggregateRange(startBucket, latestBucket);
        } catch (Exception e) {
            log.error("Aggregate job failed", e);
        }
    }
}
