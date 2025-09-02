package com.itp.ingest.sensor_ingestion_service.repository;

import com.itp.ingest.sensor_ingestion_service.model.ReadingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostgresBatchRepository implements JdbcBatchRepository {

    private static final int BATCH_CHUNK = 250;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void upsertBatch(List<ReadingMessage> batch) {
        if (invalid(batch)) return;

        final String sql = """
            INSERT INTO iot.readings (sensor_id, sensor_name, type, house_id, zone, ts, value)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (sensor_id, ts) DO UPDATE SET
                sensor_name = EXCLUDED.sensor_name,
                type        = EXCLUDED.type,
                house_id    = EXCLUDED.house_id,
                zone        = EXCLUDED.zone,
                value       = EXCLUDED.value
        """;

        final int n = batch.size();
        for (int i = 0; i < n; i += BATCH_CHUNK) {
            final int to = Math.min(n, i + BATCH_CHUNK);
            final List<ReadingMessage> chunk = batch.subList(i, to);
            jdbcTemplate.batchUpdate(sql, chunk, chunk.size(), (ps, r) -> {
                ps.setObject(1, UUID.fromString(r.sensorId()));
                ps.setString(2, r.sensorName());
                ps.setString(3, r.type());
                ps.setString(4, r.houseId());
                ps.setString(5, r.zone());
                ps.setTimestamp(6, Timestamp.from(r.timestamp())); // ok for TIMESTAMP/TIMESTAMPTZ
                ps.setDouble(7, r.value());
            });
        }
    }

    @Override
    public void insertBatch(List<ReadingMessage> batch) {
        if (invalid(batch)) return;

        // Deduplicate sensor rows to minimize conflicts
        Map<UUID, ReadingMessage> unique = new LinkedHashMap<>();
        for (ReadingMessage r : batch) {
            UUID id = UUID.fromString(r.sensorId());
            unique.putIfAbsent(id, r);
        }
        List<ReadingMessage> sensors = new ArrayList<>(unique.values());

        final String sql = """
            INSERT INTO iot.sensors (id, name, type, house_id, zone)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        final int n = sensors.size();
        for (int i = 0; i < n; i += BATCH_CHUNK) {
            final int to = Math.min(n, i + BATCH_CHUNK);
            final List<ReadingMessage> chunk = sensors.subList(i, to);
            jdbcTemplate.batchUpdate(sql, chunk, chunk.size(), (ps, r) -> {
                ps.setObject(1, UUID.fromString(r.sensorId()));
                ps.setString(2, r.sensorName());
                ps.setString(3, r.type());
                ps.setString(4, r.houseId());
                ps.setString(5, r.zone());
            });
        }
    }

    private boolean invalid(List<ReadingMessage> batch) {
        return batch == null || batch.isEmpty();
    }
}
