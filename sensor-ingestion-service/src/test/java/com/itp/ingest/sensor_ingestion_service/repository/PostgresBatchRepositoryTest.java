package com.itp.ingest.sensor_ingestion_service.repository;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PostgresBatchRepositoryTest {
    JdbcTemplate jdbc;
    PostgresBatchRepository repo;

    @BeforeEach
    void setUp() {
        jdbc = mock(JdbcTemplate.class);
        repo = new PostgresBatchRepository(jdbc);
    }

    @Test
    void insertBatch_calls_batchUpdate_with_expected_sql_and_size() {
        List<ReadingMessage> batch = List.of(
            new ReadingMessage(UUID.randomUUID().toString(), "s1", "temp", "h1", "z1", Instant.now(), 12.3),
            new ReadingMessage(UUID.randomUUID().toString(), "s2", "hum",  "h2", "z2", Instant.now(), 45.6)
        );

        ArgumentCaptor<String> sqlCap = ArgumentCaptor.forClass(String.class);

        repo.insertBatch(batch);

        verify(jdbc, times(1)).batchUpdate(sqlCap.capture(), eq(batch), eq(batch.size()), any());
        String sql = sqlCap.getValue();
        assertThat(sql)
            .contains("INSERT INTO iot.readings")
            .contains("ON CONFLICT (sensor_id, ts) DO NOTHING");
    }
}