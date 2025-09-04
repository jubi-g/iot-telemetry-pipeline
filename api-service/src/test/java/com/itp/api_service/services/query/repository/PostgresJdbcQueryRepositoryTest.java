package com.itp.api_service.services.query.repository;

import com.itp.api_service._commons.exception.DatabaseUnavailableException;
import com.itp.api_service._commons.model.dto.GroupStatsRequest;
import com.itp.api_service._commons.model.dto.SensorStatsRequest;
import com.itp.api_service.api.v1.model.ErrorType;
import com.itp.api_service.services.query.model.dto.TimeWindowRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostgresJdbcQueryRepositoryTest {

    JdbcTemplate jdbc;
    PostgresJdbcQueryRepository repo;

    @BeforeEach
    void setup() {
        jdbc = mock(JdbcTemplate.class);
        repo = new PostgresJdbcQueryRepository(jdbc);
    }

    @Test
    void sensorStats_mapsDataIntegrity_toValidateError() {
        when(jdbc.query(
            anyString(),
            any(RowMapper.class),
            any(), any(), any()
        )).thenThrow(new DataIntegrityViolationException("conflict"));

        var req = SensorStatsRequest.builder()
            .sensorId(UUID.randomUUID())
            .window(TimeWindowRequest.builder()
                .from(Instant.now().minusSeconds(60))
                .to(Instant.now())
                .build())
            .build();

        DatabaseUnavailableException ex = assertThrows(
            DatabaseUnavailableException.class,
            () -> repo.sensorStats(req)
        );
        assertEquals(ErrorType.DB_VALIDATE_ERROR, ex.getError());
    }

    @Test
    void groupStats_mapsOptimisticLock_toValidateError() {
        when(jdbc.query(
            anyString(),
            any(RowMapper.class),
            any(), any(), any(), any(), any()
        )).thenThrow(new OptimisticLockingFailureException("locked"));

        var req = GroupStatsRequest.builder()
            .houseId("H").zone("Z").type("temp")
            .window(TimeWindowRequest.builder()
                .from(Instant.now().minusSeconds(60))
                .to(Instant.now())
                .build())
            .build();

        DatabaseUnavailableException ex = assertThrows(
            DatabaseUnavailableException.class,
            () -> repo.groupStats(req)
        );
        assertEquals(ErrorType.DB_VALIDATE_ERROR, ex.getError());
    }

    @Test
    void sensorStats_mapsOther_toDatabaseUnavailable() {
        when(jdbc.query(
            anyString(),
            any(RowMapper.class),
            any(), any(), any()
        )).thenThrow(new RuntimeException());

        var req = SensorStatsRequest.builder()
            .sensorId(UUID.randomUUID())
            .window(TimeWindowRequest.builder()
                .from(Instant.now().minusSeconds(60))
                .to(Instant.now())
                .build())
            .build();

        DatabaseUnavailableException ex = assertThrows(
            DatabaseUnavailableException.class,
            () -> repo.sensorStats(req)
        );
        assertEquals(ErrorType.DATABASE_UNAVAILABLE, ex.getError());
    }

}
