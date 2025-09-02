package com.itp.aggregate_service.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MinuteAggregateRepositoryTest {
    JdbcTemplate jdbcTemplate;
    MinuteAggregateRepository repository;

    @BeforeEach
    void setup() {
        jdbcTemplate = mock(JdbcTemplate.class);
        repository = new MinuteAggregateRepository(jdbcTemplate);
    }

    @Test
    void upsertAggregate_buildsExpectedSQL() {
        Instant bucket = Instant.parse("2025-09-02T11:21:00Z");
        ArgumentCaptor<String> sqlCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<org.springframework.jdbc.core.PreparedStatementSetter> pssCap =
            ArgumentCaptor.forClass(org.springframework.jdbc.core.PreparedStatementSetter.class);

        repository.upsertAggregate(bucket);

        verify(jdbcTemplate, times(1)).update(sqlCap.capture(), pssCap.capture());
        String sql = sqlCap.getValue();
        assertThat(sql).contains("INSERT INTO iot.agg_sensor_minute")
            .contains("FROM iot.readings")
            .contains("ON CONFLICT");
    }

    @Test
    void upsertGroupAggregate_buildsExpectedSQL() {
        Instant bucket = Instant.parse("2025-09-02T11:21:00Z");
        ArgumentCaptor<String> sqlCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<org.springframework.jdbc.core.PreparedStatementSetter> pssCap =
            ArgumentCaptor.forClass(org.springframework.jdbc.core.PreparedStatementSetter.class);

        repository.upsertGroupAggregate(bucket);

        verify(jdbcTemplate, times(1)).update(sqlCap.capture(), pssCap.capture());
        String sql = sqlCap.getValue();
        assertThat(sql).contains("INSERT INTO iot.agg_group_minute")
            .contains("FROM iot.readings")
            .contains("ON CONFLICT");
    }
}