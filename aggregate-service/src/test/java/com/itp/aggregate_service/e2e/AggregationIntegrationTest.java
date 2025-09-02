package com.itp.aggregate_service.e2e;

import com.itp.aggregate_service.service.AggregationService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AggregationIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("itp")
            .withUsername("itp")
            .withPassword("itp");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        r.add("spring.flyway.enabled", () -> "true");
        r.add("spring.flyway.create-schemas", () -> "true");
        r.add("spring.flyway.schemas", () -> "iot_agg");
        r.add("spring.flyway.default-schema", () -> "iot_agg");
        r.add("spring.flyway.locations", () -> "classpath:db/migration/aggregate");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired AggregationService service;

    @BeforeAll
    static void migrate() {
        Flyway.configure()
            .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
            .schemas("iot_agg")
            .locations("classpath:db/migration/aggregate")
            .load()
            .migrate();
    }

    @BeforeEach
    void setupReadingsTable() {
        jdbc.execute("CREATE SCHEMA IF NOT EXISTS iot");
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS iot.readings (
              sensor_id  UUID NOT NULL,
              sensor_name TEXT,
              type        TEXT,
              house_id    TEXT,
              zone        TEXT,
              ts          TIMESTAMPTZ NOT NULL,
              value       DOUBLE PRECISION NOT NULL
            )
        """);
        jdbc.execute("CREATE UNIQUE INDEX IF NOT EXISTS ux_readings_sensor_ts ON iot.readings(sensor_id, ts)");
    }

    @Test
    @DisplayName("Aggregates are computed for a 2-minute window")
    void aggregatesForWindow() {
        Instant t0 = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        Instant t1 = t0.plus(1, ChronoUnit.MINUTES);

        UUID s1 = UUID.randomUUID();
        UUID s2 = UUID.randomUUID();

        // 2 minutes, 2 sensors → expect 4 rows in sensor-minute table
        insertReading(s1, "s1", "temp", "H1", "Z1", t0, 10.0);
        insertReading(s2, "s2", "temp", "H1", "Z1", t0, 20.0);
        insertReading(s1, "s1", "temp", "H1", "Z1", t1, 30.0);
        insertReading(s2, "s2", "temp", "H1", "Z1", t1, 40.0);

        service.aggregateRangedWindow(t0, t1);

        Integer sensorCnt = jdbc.queryForObject("SELECT COUNT(*) FROM iot.agg_sensor_minute", Integer.class);
        Integer groupCnt  = jdbc.queryForObject("SELECT COUNT(*) FROM iot.agg_group_minute", Integer.class);

        assertThat(sensorCnt).isNotNull().isEqualTo(4);
        assertThat(groupCnt).isNotNull().isEqualTo(2); // two minutes, same (house,zone,type) → 2 rows
    }

    private void insertReading(UUID id, String name, String type, String house, String zone, Instant ts, double v) {
        jdbc.update("""
            INSERT INTO iot.readings(sensor_id, sensor_name, type, house_id, zone, ts, value)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """, ps -> {
            ps.setObject(1, id);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, house);
            ps.setString(5, zone);
            ps.setObject(6, OffsetDateTime.ofInstant(ts, ZoneOffset.UTC));
            ps.setDouble(7, v);
        });
    }
}
