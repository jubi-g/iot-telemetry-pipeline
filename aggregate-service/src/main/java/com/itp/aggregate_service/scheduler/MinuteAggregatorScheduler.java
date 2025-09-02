package com.itp.aggregate_service.scheduler;

import com.itp.aggregate_service.config.AggConfig;
import com.itp.aggregate_service.service.AggregationService;
import com.itp.aggregate_service.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.itp.aggregate_service.utils.TimeUtil.latestUtcMinute;

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
            Instant endTime = latestUtcMinute(now);
            Instant startTime = service.computeStartTime(endTime);
            if (startTime.isAfter(endTime)) {
                log.info("Nothing to aggregate: startTime={} > endTime={}", startTime, endTime);
                return;
            }
            service.aggregateRangedWindow(startTime, endTime);
        } catch (Exception e) {
            log.error("Aggregate job failed", e);
        }
    }
}
