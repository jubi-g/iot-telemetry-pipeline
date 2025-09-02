package com.itp.ingest.sensor_ingestion_service.repository;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReadingBatchRepository implements JdbcBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void upsertBatch(List<ReadingMessage> batch) {
        String sql = """
          INSERT INTO iot.sensors (id, name, type, house_id, zone)
          VALUES (?, ?, ?, ?, ?)
          ON CONFLICT (id) DO NOTHING
        """;
        // Insert sensors if missing (idempotent)
        jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, r) -> {
            ps.setObject(1, UUID.fromString(r.sensorId()));
            ps.setString(2, r.sensorName());
            ps.setString(3, r.type());
            ps.setString(4, r.houseId());
            ps.setString(5, r.zone());
        });
    }

    @Override
    public void insertBatch(List<ReadingMessage> batch) {
        String sql = """
          INSERT INTO iot.readings (sensor_id, sensor_name, type, house_id, zone, ts, value)
          VALUES (?, ?, ?, ?, ?, ?, ?)
          ON CONFLICT (sensor_id, ts) DO NOTHING
        """;
        jdbcTemplate.batchUpdate(sql, batch, batch.size(), (ps, r) -> {
            ps.setObject(1, UUID.fromString(r.sensorId()));
            ps.setString(2, r.sensorName());
            ps.setString(3, r.type());
            ps.setString(4, r.houseId());
            ps.setString(5, r.zone());
            ps.setTimestamp(6, Timestamp.from(r.timestamp()));
            ps.setDouble(7, r.value());
        });
    }
}
