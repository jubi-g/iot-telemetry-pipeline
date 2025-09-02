package com.itp.aggregate_service.service;

import com.itp.aggregate_service.config.AggConfig;
import com.itp.aggregate_service.repository.MinuteAggregateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MinuteAggregationServiceTest {

    AggConfig config;
    MinuteAggregationService service;
    MinuteAggregateRepository repository;

    @BeforeEach
    void setup() {
        repository = mock(MinuteAggregateRepository.class);
        config  = new AggConfig();
        service  = new MinuteAggregationService(config, repository);
    }

    @Test
    void computeStartTime_appliesDelayAndFloorsToMinute() {
        config.setDelaySeconds(75);
        Instant now = Instant.parse("2025-09-02T11:21:33Z");
        Instant latest = service.computeStartTime(now);

        // now - 75s = 11:21:18Z -> floor to 11:21:00Z
        assertThat(latest).isEqualTo(Instant.parse("2025-09-02T11:21:00Z"));
    }

    @Test
    void computeStartTime_usesLatest_whenCatchupOff() {
        config.setCatchupOnStart(false);
        Instant latest = Instant.parse("2025-09-02T11:21:00Z");
        Instant start = service.computeStartTime(latest);
        assertThat(start).isEqualTo(latest);
        verifyNoInteractions(repository);
    }

    @Test
    void computeStartTime_isMaxOf_nextAfterLastAgg_and_firstData_whenCatchupOn() {
        config.setCatchupOnStart(true);
        // last aggregated at 11:05 -> next = 11:06
        when(repository.lastAggregatedSensor()).thenReturn(Instant.parse("2025-09-02T11:05:13Z"));
        // first data at 11:04 -> first bucket = 11:04
        when(repository.oldestReading()).thenReturn(Instant.parse("2025-09-02T11:04:59Z"));

        Instant latest = Instant.parse("2025-09-02T11:21:00Z");
        Instant start = service.computeStartTime(latest);

        assertThat(start).isEqualTo(Instant.parse("2025-09-02T11:06:00Z"));
    }

    @Test
    void computeStartTime_handlesNulls() {
        config.setCatchupOnStart(true);
        when(repository.lastAggregatedSensor()).thenReturn(null);
        when(repository.oldestReading()).thenReturn(Instant.parse("2025-09-02T11:04:00Z"));

        Instant latest = Instant.parse("2025-09-02T11:21:00Z");
        Instant start = service.computeStartTime(latest);

        assertThat(start).isEqualTo(Instant.parse("2025-09-02T11:04:00Z"));
    }

    @Test
    void aggregateRangedWindow_callUpsertsForEachMinute() {
        Instant start  = Instant.parse("2025-09-02T11:00:00Z");
        Instant end = Instant.parse("2025-09-02T11:03:00Z");

        when(repository.upsertAggregate(start.plus(0, ChronoUnit.MINUTES))).thenReturn(2);
        when(repository.upsertAggregate(start.plus(1, ChronoUnit.MINUTES))).thenReturn(0);
        when(repository.upsertAggregate(start.plus(2, ChronoUnit.MINUTES))).thenReturn(1);
        when(repository.upsertAggregate(start.plus(3, ChronoUnit.MINUTES))).thenReturn(3);

        when(repository.upsertGroupAggregate(start.plus(0, ChronoUnit.MINUTES))).thenReturn(1);
        when(repository.upsertGroupAggregate(start.plus(1, ChronoUnit.MINUTES))).thenReturn(1);
        when(repository.upsertGroupAggregate(start.plus(2, ChronoUnit.MINUTES))).thenReturn(1);
        when(repository.upsertGroupAggregate(start.plus(3, ChronoUnit.MINUTES))).thenReturn(1);

        service.aggregateRangedWindow(start, end);

        // Verify each minute was called exactly once for both methods
        for (int i = 0; i < 4; i++) {
            Instant bucket = start.plus(i, ChronoUnit.MINUTES);
            verify(repository, times(1)).upsertAggregate(bucket);
            verify(repository, times(1)).upsertGroupAggregate(bucket);
        }
        verifyNoMoreInteractions(repository);
    }
}