package com.itp.aggregate_service.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Repository
@RequiredArgsConstructor
public class MinuteAggregateRepository implements AggregateReadingRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Instant oldestReading() {
        String sql = "SELECT date_trunc('minute', MIN(ts)) FROM iot.readings";
        return jdbcTemplate.queryForObject(sql, Instant.class);
    }

    @Override
    public Instant lastAggregatedSensor() {
        var sql = "SELECT MAX(bucket_minute) FROM iot.agg_sensor_minute";
        return jdbcTemplate.queryForObject(sql, Instant.class);
    }

    @Override
    public int upsertAggregate(Instant bucket) {
        var sql = """
            INSERT INTO iot.agg_sensor_minute
              (bucket_minute, sensor_id, sensor_name, type, house_id, zone, cnt, min_val, max_val, avg_val, median)
            SELECT
              date_trunc('minute', ts)                    AS bucket_minute,
              sensor_id,
              MIN(sensor_name)                            AS sensor_name,  -- any value
              type,
              MIN(house_id)                               AS house_id,     -- any value
              MIN(zone)                                   AS zone,         -- any value
              COUNT(*)                                    AS cnt,
              MIN(value)                                  AS min_val,
              MAX(value)                                  AS max_val,
              AVG(value)                                  AS avg_val,
              percentile_cont(0.5) WITHIN GROUP (ORDER BY value) AS median
            FROM iot.readings
            WHERE ts >= ? AND ts < (? + interval '1 minute')
            GROUP BY bucket_minute, sensor_id, type
            ON CONFLICT (bucket_minute, sensor_id)
            DO UPDATE SET
              cnt        = EXCLUDED.cnt,
              min_val    = EXCLUDED.min_val,
              max_val    = EXCLUDED.max_val,
              avg_val    = EXCLUDED.avg_val,
              median     = EXCLUDED.median,
              sensor_name= EXCLUDED.sensor_name,
              house_id   = EXCLUDED.house_id,
              zone       = EXCLUDED.zone;
        """;
        return jdbcTemplate.update(sql, ps -> {
            var dt = OffsetDateTime.ofInstant(bucket, ZoneOffset.UTC);
            ps.setObject(1, dt);
            ps.setObject(2, dt);
        });
    }

    @Override
    public int upsertGroupAggregate(Instant bucket) {
        var sql = """
          INSERT INTO iot.agg_group_minute
            (bucket_minute, house_id, zone, type, cnt, min_val, max_val, avg_val, median)
          SELECT
            date_trunc('minute', ts) AS bucket_minute,
            house_id, zone, type,
            COUNT(*)                  AS cnt,
            MIN(value)                AS min_val,
            MAX(value)                AS max_val,
            AVG(value)                AS avg_val,
            percentile_cont(0.5) WITHIN GROUP (ORDER BY value) AS median
          FROM iot.readings
          WHERE ts >= ? AND ts < (? + interval '1 minute')
          GROUP BY bucket_minute, house_id, zone, type
          ON CONFLICT (bucket_minute, house_id, zone, type)
          DO UPDATE SET
            cnt=EXCLUDED.cnt,
            min_val=EXCLUDED.min_val,
            max_val=EXCLUDED.max_val,
            avg_val=EXCLUDED.avg_val,
            median=EXCLUDED.median;
          """;
        return jdbcTemplate.update(sql, ps -> {
            var dt = OffsetDateTime.ofInstant(bucket, ZoneOffset.UTC);
            ps.setObject(1, dt);
            ps.setObject(2, dt);
        });
    }
}
