package com.itp.aggregate_service.scheduler;

import com.itp.aggregate_service.config.AggConfig;
import com.itp.aggregate_service.service.AggregationService;
import io.micrometer.core.annotation.Timed;
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
    @Timed(value = "agg.run", histogram = true) // TODO: use this metric
    @Scheduled(fixedDelayString = "30000", initialDelayString = "15000")
    public void runSchedule() {
        Instant end = latestUtcMinute(Instant.now().minusSeconds(aggConfig.getDelaySeconds()));
        Instant start = service.computeStartTime(end);
        if (start.isAfter(end)) {
            log.debug("Skip aggregation: start={} > end={}", start, end);
            return;
        }

        try {
            log.info("Aggregating window start={} end={}", start, end);
            service.aggregateRangedWindow(start, end);
        } catch (Exception e) {
            log.error("Aggregation failed", e);
        }
    }
}
